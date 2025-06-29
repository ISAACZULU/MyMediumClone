package org.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.dto.ArticleResponseDto;
import org.example.entity.*;
import org.example.repository.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmailDigestService {

    private static final Logger log = LoggerFactory.getLogger(EmailDigestService.class);

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final ReadingHistoryRepository readingHistoryRepository;
    private final RecommendationService recommendationService;

    public EmailDigestService(UserRepository userRepository, ArticleRepository articleRepository, ReadingHistoryRepository readingHistoryRepository, RecommendationService recommendationService) {
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.readingHistoryRepository = readingHistoryRepository;
        this.recommendationService = recommendationService;
    }

    /**
     * Send daily email digests to active users
     * Runs every day at 8:00 AM
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendDailyEmailDigests() {
        log.info("Starting daily email digest generation");
        
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        List<User> activeUsers = userRepository.findByLastLoginAfter(yesterday);
        
        for (User user : activeUsers) {
            try {
                generateAndSendDailyDigest(user);
            } catch (Exception e) {
                log.error("Failed to send daily digest to user {}: {}", user.getEmail(), e.getMessage());
            }
        }
        
        log.info("Completed daily email digest generation for {} users", activeUsers.size());
    }

    /**
     * Send weekly email digests to all users
     * Runs every Sunday at 9:00 AM
     */
    @Scheduled(cron = "0 0 9 ? * SUN")
    public void sendWeeklyEmailDigests() {
        log.info("Starting weekly email digest generation");
        
        List<User> allUsers = userRepository.findAll();
        
        for (User user : allUsers) {
            try {
                generateAndSendWeeklyDigest(user);
            } catch (Exception e) {
                log.error("Failed to send weekly digest to user {}: {}", user.getEmail(), e.getMessage());
            }
        }
        
        log.info("Completed weekly email digest generation for {} users", allUsers.size());
    }

    private void generateAndSendDailyDigest(User user) {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        
        // Get user's reading history from yesterday
        List<ReadingHistory> recentReads = readingHistoryRepository
                .findByUserIdAndReadAtAfterOrderByReadAtDesc(user.getId(), yesterday);
        
        // Get recommended articles
        List<ArticleResponseDto> recommendations = recommendationService.getPersonalizedFeed(user.getUsername(), 5);
        
        // Get trending articles in user's preferred categories
        List<Article> trendingArticles = getTrendingArticlesForUser(user, 3);
        
        // Generate email content
        String emailContent = generateDailyDigestContent(user, recentReads, recommendations, trendingArticles);
        
        // Send email (implement actual email sending logic)
        sendEmail(user.getEmail(), "Your Daily Reading Digest", emailContent);
        
        log.info("Sent daily digest to user: {}", user.getEmail());
    }

    private void generateAndSendWeeklyDigest(User user) {
        LocalDateTime weekAgo = LocalDateTime.now().minusWeeks(1);
        
        // Get user's reading statistics for the week
        List<ReadingHistory> weeklyReads = readingHistoryRepository
                .findByUserIdAndReadAtAfterOrderByReadAtDesc(user.getId(), weekAgo);
        
        // Get user's engagement statistics
        Map<String, Object> engagementStats = getWeeklyEngagementStats(user.getId(), weekAgo);
        
        // Get top articles from the week
        List<Article> topWeeklyArticles = getTopWeeklyArticles(5);
        
        // Get personalized recommendations
        List<ArticleResponseDto> recommendations = recommendationService.getPersonalizedFeed(user.getUsername(), 10);
        
        // Generate email content
        String emailContent = generateWeeklyDigestContent(user, weeklyReads, engagementStats, 
                topWeeklyArticles, recommendations);
        
        // Send email
        sendEmail(user.getEmail(), "Your Weekly Reading Summary", emailContent);
        
        log.info("Sent weekly digest to user: {}", user.getEmail());
    }

    private List<Article> getTrendingArticlesForUser(User user, int limit) {
        // Get user's preferred tags from reading history
        Set<String> preferredTags = getUserPreferredTags(user.getId());
        
        if (preferredTags.isEmpty()) {
            // If no preferences, return general trending articles
            return articleRepository.findTopArticlesByClapsInLastDays(LocalDateTime.now().minusDays(7), PageRequest.of(0, limit));
        }
        
        return articleRepository.findTrendingArticlesByTags(preferredTags, LocalDateTime.now().minusDays(7), PageRequest.of(0, limit));
    }

    private Set<String> getUserPreferredTags(Long userId) {
        LocalDateTime monthAgo = LocalDateTime.now().minusMonths(1);
        List<ReadingHistory> recentReads = readingHistoryRepository
                .findByUserIdAndReadAtAfterOrderByReadAtDesc(userId, monthAgo);
        
        return recentReads.stream()
                .flatMap(history -> history.getArticle().getTags().stream())
                .collect(Collectors.groupingBy(
                        tag -> tag.getName(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    private Map<String, Object> getWeeklyEngagementStats(Long userId, LocalDateTime since) {
        Map<String, Object> stats = new HashMap<>();
        
        // Articles read
        long articlesRead = readingHistoryRepository.countByUserIdAndReadAtAfter(userId, since);
        stats.put("articlesRead", articlesRead);
        
        // Reading time
        long totalReadingTime = readingHistoryRepository
                .findByUserIdAndReadAtAfterOrderByReadAtDesc(userId, since)
                .stream()
                .mapToLong(history -> history.getArticle().getReadTimeMinutes())
                .sum();
        stats.put("totalReadingTime", totalReadingTime);
        
        // Claps given
        // Comments made
        // Bookmarks created
        
        return stats;
    }

    private List<Article> getTopWeeklyArticles(int limit) {
        return articleRepository.findTopArticlesByClapsInLastDays(LocalDateTime.now().minusDays(7), org.springframework.data.domain.PageRequest.of(0, limit));
    }

    private String generateDailyDigestContent(User user, List<ReadingHistory> recentReads, 
                                            List<ArticleResponseDto> recommendations, List<Article> trendingArticles) {
        StringBuilder content = new StringBuilder();
        
        content.append("Hello ").append(user.getUsername()).append(",\n\n");
        content.append("Here's your daily reading digest:\n\n");
        
        // Recent reads section
        if (!recentReads.isEmpty()) {
            content.append("📚 Articles you read yesterday:\n");
            recentReads.stream().limit(3).forEach(read -> {
                content.append("• ").append(read.getArticle().getTitle())
                       .append(" by ").append(read.getArticle().getAuthor().getUsername())
                       .append(" (").append(read.getArticle().getReadTimeMinutes()).append(" min read)\n");
            });
            content.append("\n");
        }
        
        // Recommendations section
        if (!recommendations.isEmpty()) {
            content.append("🎯 Recommended for you:\n");
            recommendations.forEach(article -> {
                content.append("• ").append(article.getTitle())
                       .append(" by ").append(article.getAuthor().getUsername())
                       .append(" (").append(article.getReadTimeMinutes()).append(" min read)\n");
            });
            content.append("\n");
        }
        
        // Trending section
        if (!trendingArticles.isEmpty()) {
            content.append("🔥 Trending today:\n");
            trendingArticles.forEach(article -> {
                content.append("• ").append(article.getTitle())
                       .append(" by ").append(article.getAuthor().getUsername())
                       .append(" (").append(article.getLikeCount()).append(" claps)\n");
            });
            content.append("\n");
        }
        
        content.append("Happy reading!\n");
        content.append("The Medium Clone Team");
        
        return content.toString();
    }

    private String generateWeeklyDigestContent(User user, List<ReadingHistory> weeklyReads, 
                                             Map<String, Object> engagementStats, 
                                             List<Article> topWeeklyArticles, 
                                             List<ArticleResponseDto> recommendations) {
        StringBuilder content = new StringBuilder();
        
        content.append("Hello ").append(user.getUsername()).append(",\n\n");
        content.append("Here's your weekly reading summary:\n\n");
        
        // Weekly stats
        content.append("📊 Your week in numbers:\n");
        content.append("• Articles read: ").append(engagementStats.get("articlesRead")).append("\n");
        content.append("• Total reading time: ").append(engagementStats.get("totalReadingTime")).append(" minutes\n");
        content.append("\n");
        
        // Top articles of the week
        if (!topWeeklyArticles.isEmpty()) {
            content.append("🏆 Top articles this week:\n");
            topWeeklyArticles.forEach(article -> {
                content.append("• ").append(article.getTitle())
                       .append(" by ").append(article.getAuthor().getUsername())
                       .append(" (").append(article.getLikeCount()).append(" claps)\n");
            });
            content.append("\n");
        }
        
        // Personalized recommendations
        if (!recommendations.isEmpty()) {
            content.append("🎯 Recommended for you:\n");
            recommendations.stream().limit(5).forEach(article -> {
                content.append("• ").append(article.getTitle())
                       .append(" by ").append(article.getAuthor().getUsername())
                       .append(" (").append(article.getReadTimeMinutes()).append(" min read)\n");
            });
            content.append("\n");
        }
        
        content.append("Keep exploring great content!\n");
        content.append("The Medium Clone Team");
        
        return content.toString();
    }

    private void sendEmail(String email, String subject, String content) {
        // TODO: Implement actual email sending logic
        // This could use JavaMailSender, SendGrid, or any other email service
        log.info("Would send email to {}: {} - {}", email, subject, content.substring(0, Math.min(100, content.length())));
    }
} 