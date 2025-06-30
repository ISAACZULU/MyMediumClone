package org.example.service;

import org.example.dto.ArticleResponseDto;
import org.example.entity.*;
import org.example.repository.*;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.example.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvancedRecommendationService {
    
    private final ArticleRepository articleRepository;
    
    private final ReadingHistoryRepository readingHistoryRepository;
    
    private final ClapRepository clapRepository;
    
    private final BookmarkRepository bookmarkRepository;
    
    private final CommentRepository commentRepository;
    
    private final UserRepository userRepository;
    
    // Algorithm weights (configurable)
    private static final double CONTENT_SIMILARITY_WEIGHT = 0.3;
    private static final double USER_BEHAVIOR_WEIGHT = 0.4;
    private static final double POPULARITY_WEIGHT = 0.2;
    private static final double RECENCY_WEIGHT = 0.1;
    
    public List<ArticleResponseDto> getPersonalizedRecommendations(String username, int limit) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Get user behavior data
        UserBehaviorProfile behaviorProfile = buildUserBehaviorProfile(user);
        
        // Get candidate articles
        List<Article> candidateArticles = getCandidateArticles(user, behaviorProfile);
        
        // Score and rank articles
        List<ScoredArticle> scoredArticles = candidateArticles.stream()
                .map(article -> new ScoredArticle(article, calculatePersonalizedScore(article, behaviorProfile)))
                .sorted((a, b) -> Double.compare(b.score, a.score))
                .limit(limit)
                .collect(Collectors.toList());
        
        return scoredArticles.stream()
                .map(sa -> toArticleResponseDto(sa.article))
                .collect(Collectors.toList());
    }
    
    public List<ArticleResponseDto> getCollaborativeFilteringRecommendations(String username, int limit) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Find similar users based on reading history
        List<User> similarUsers = findSimilarUsers(user);
        
        // Get articles liked by similar users
        Set<Long> recommendedArticleIds = new HashSet<>();
        for (User similarUser : similarUsers) {
            List<Clap> userClaps = clapRepository.findByUser(similarUser);
            for (Clap clap : userClaps) {
                if (clap.getClapCount() > 5) { // Only consider strong positive signals
                    recommendedArticleIds.add(clap.getArticle().getId());
                }
            }
        }
        
        // Get articles and score them
        List<Article> articles = articleRepository.findAllById(recommendedArticleIds);
        List<ScoredArticle> scoredArticles = articles.stream()
                .map(article -> new ScoredArticle(article, calculateCollaborativeScore(article, similarUsers)))
                .sorted((a, b) -> Double.compare(b.score, a.score))
                .limit(limit)
                .collect(Collectors.toList());
        
        return scoredArticles.stream()
                .map(sa -> toArticleResponseDto(sa.article))
                .collect(Collectors.toList());
    }
    
    public List<ArticleResponseDto> getContentBasedRecommendations(Long articleId, String username, int limit) {
        Article sourceArticle = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));
        
        // Get articles with similar content characteristics
        List<Article> similarArticles = findContentSimilarArticles(sourceArticle, limit * 2);
        
        // Score based on content similarity
        List<ScoredArticle> scoredArticles = similarArticles.stream()
                .filter(article -> !article.getId().equals(articleId))
                .map(article -> new ScoredArticle(article, calculateContentSimilarity(sourceArticle, article)))
                .sorted((a, b) -> Double.compare(b.score, a.score))
                .limit(limit)
                .collect(Collectors.toList());
        
        return scoredArticles.stream()
                .map(sa -> toArticleResponseDto(sa.article))
                .collect(Collectors.toList());
    }
    
    private UserBehaviorProfile buildUserBehaviorProfile(User user) {
        UserBehaviorProfile profile = new UserBehaviorProfile();
        
        // Reading history analysis
        List<ReadingHistory> readingHistory = readingHistoryRepository.findByUserOrderByReadAtDesc(user);
        profile.readingHistory = readingHistory;
        
        // Extract interests from read articles
        Set<String> interests = new HashSet<>();
        for (ReadingHistory history : readingHistory) {
            interests.addAll(history.getArticle().getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toSet()));
        }
        profile.interests = interests;
        
        // Engagement analysis
        List<Clap> claps = clapRepository.findByUser(user);
        profile.engagementLevel = calculateEngagementLevel(claps);
        
        // Reading patterns
        profile.averageReadTime = calculateAverageReadTime(readingHistory);
        profile.preferredContentLength = calculatePreferredContentLength(readingHistory);
        
        return profile;
    }
    
    private List<Article> getCandidateArticles(User user, UserBehaviorProfile profile) {
        Set<Long> readArticleIds = profile.readingHistory.stream()
                .map(h -> h.getArticle().getId())
                .collect(Collectors.toSet());
        
        // Get articles based on user interests
        List<Article> interestBasedArticles = new ArrayList<>();
        if (!profile.interests.isEmpty()) {
            interestBasedArticles = articleRepository.findByTags(profile.interests, 
                    org.springframework.data.domain.PageRequest.of(0, 50)).getContent();
        }
        
        // Get trending articles
        List<Article> trendingArticles = articleRepository.findTrending(
                org.springframework.data.domain.PageRequest.of(0, 30)).getContent();
        
        // Combine and filter
        Set<Article> candidateSet = new HashSet<>();
        candidateSet.addAll(interestBasedArticles);
        candidateSet.addAll(trendingArticles);
        
        return candidateSet.stream()
                .filter(article -> !readArticleIds.contains(article.getId()))
                .filter(article -> article.isPublished())
                .collect(Collectors.toList());
    }
    
    private double calculatePersonalizedScore(Article article, UserBehaviorProfile profile) {
        double score = 0.0;
        
        // Content similarity score
        double contentSimilarity = calculateContentSimilarityForUser(article, profile);
        score += contentSimilarity * CONTENT_SIMILARITY_WEIGHT;
        
        // User behavior score
        double behaviorScore = calculateBehaviorScore(article, profile);
        score += behaviorScore * USER_BEHAVIOR_WEIGHT;
        
        // Popularity score
        double popularityScore = calculatePopularityScore(article);
        score += popularityScore * POPULARITY_WEIGHT;
        
        // Recency score
        double recencyScore = calculateRecencyScore(article);
        score += recencyScore * RECENCY_WEIGHT;
        
        return score;
    }
    
    private double calculateContentSimilarityForUser(Article article, UserBehaviorProfile profile) {
        Set<String> articleTags = article.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
        
        Set<String> intersection = new HashSet<>(articleTags);
        intersection.retainAll(profile.interests);
        
        if (profile.interests.isEmpty()) {
            return 0.0;
        }
        
        return intersection.size() / (double) profile.interests.size();
    }
    
    private double calculateBehaviorScore(Article article, UserBehaviorProfile profile) {
        double score = 0.0;
        
        // Content length preference
        int articleLength = article.getContent().length();
        if (Math.abs(articleLength - profile.preferredContentLength) < 1000) {
            score += 0.3;
        }
        
        // Author preference (if user has read articles from this author before)
        boolean hasReadFromAuthor = profile.readingHistory.stream()
                .anyMatch(h -> h.getArticle().getAuthor().equals(article.getAuthor()));
        if (hasReadFromAuthor) {
            score += 0.2;
        }
        
        // Engagement level matching
        if (profile.engagementLevel > 0.7 && article.getLikeCount() > 100) {
            score += 0.2;
        }
        
        return score;
    }
    
    private double calculatePopularityScore(Article article) {
        double viewScore = Math.log10(article.getViewCount() + 1) / 10.0;
        double likeScore = Math.log10(article.getLikeCount() + 1) / 10.0;
        double commentScore = Math.log10(article.getCommentCount() + 1) / 10.0;
        
        return (viewScore + likeScore + commentScore) / 3.0;
    }
    
    private double calculateRecencyScore(Article article) {
        if (article.getPublishedAt() == null) {
            return 0.5;
        }
        
        long daysSincePublished = ChronoUnit.DAYS.between(article.getPublishedAt(), LocalDateTime.now());
        return Math.max(0.1, 1.0 - (daysSincePublished / 365.0));
    }
    
    private List<User> findSimilarUsers(User user) {
        // Find users with similar reading patterns
        List<ReadingHistory> userHistory = readingHistoryRepository.findByUserOrderByReadAtDesc(user);
        Set<Long> userReadArticles = userHistory.stream()
                .map(h -> h.getArticle().getId())
                .collect(Collectors.toSet());
        
        // This is a simplified implementation
        // In production, you'd use more sophisticated collaborative filtering algorithms
        return userRepository.findAll().stream()
                .filter(u -> !u.equals(user))
                .limit(10)
                .collect(Collectors.toList());
    }
    
    private double calculateCollaborativeScore(Article article, List<User> similarUsers) {
        // Count how many similar users have engaged with this article
        long engagementCount = similarUsers.stream()
                .mapToLong(user -> {
                    Optional<Clap> clap = clapRepository.findByArticleAndUser(article, user);
                    return clap.map(Clap::getClapCount).orElse(0);
                })
                .sum();
        
        return Math.log10(engagementCount + 1) / 10.0;
    }
    
    private List<Article> findContentSimilarArticles(Article sourceArticle, int limit) {
        Set<String> sourceTags = sourceArticle.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
        
        return articleRepository.findByTags(sourceTags, 
                org.springframework.data.domain.PageRequest.of(0, limit)).getContent();
    }
    
    private double calculateContentSimilarity(Article article1, Article article2) {
        Set<String> tags1 = article1.getTags().stream().map(Tag::getName).collect(Collectors.toSet());
        Set<String> tags2 = article2.getTags().stream().map(Tag::getName).collect(Collectors.toSet());
        
        Set<String> intersection = new HashSet<>(tags1);
        intersection.retainAll(tags2);
        
        Set<String> union = new HashSet<>(tags1);
        union.addAll(tags2);
        
        if (union.isEmpty()) {
            return 0.0;
        }
        
        return intersection.size() / (double) union.size(); // Jaccard similarity
    }
    
    private double calculateEngagementLevel(List<Clap> claps) {
        if (claps.isEmpty()) {
            return 0.0;
        }
        
        double totalClaps = claps.stream().mapToInt(Clap::getClapCount).sum();
        return Math.min(1.0, totalClaps / 100.0); // Normalize to 0-1
    }
    
    private double calculateAverageReadTime(List<ReadingHistory> readingHistory) {
        if (readingHistory.isEmpty()) {
            return 5.0; // Default 5 minutes
        }
        
        return readingHistory.stream()
                .mapToDouble(h -> h.getArticle().getReadTimeMinutes() != null ? 
                        h.getArticle().getReadTimeMinutes() : 5.0)
                .average()
                .orElse(5.0);
    }
    
    private int calculatePreferredContentLength(List<ReadingHistory> readingHistory) {
        if (readingHistory.isEmpty()) {
            return 2000; // Default 2000 characters
        }
        
        return (int) readingHistory.stream()
                .mapToInt(h -> h.getArticle().getContent().length())
                .average()
                .orElse(2000);
    }
    
    private ArticleResponseDto toArticleResponseDto(Article article) {
        ArticleResponseDto dto = new ArticleResponseDto();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setSlug(article.getSlug());
        dto.setSummary(article.getSummary());
        dto.setCoverImageUrl(article.getCoverImageUrl());
        dto.setReadTimeMinutes(article.getReadTimeMinutes());
        dto.setViewCount(article.getViewCount());
        dto.setLikeCount(article.getLikeCount());
        dto.setCommentCount(article.getCommentCount());
        dto.setPublished(article.isPublished());
        dto.setCreatedAt(article.getCreatedAt());
        dto.setPublishedAt(article.getPublishedAt());
        dto.setAuthor(new org.example.dto.UserProfileDto(
                article.getAuthor().getId(),
                article.getAuthor().getUsername(),
                article.getAuthor().getEmail(),
                article.getAuthor().getBio(),
                article.getAuthor().getProfileImageUrl(),
                article.getAuthor().getCreatedAt()
        ));
        dto.setTags(article.getTags().stream().map(Tag::getName).collect(Collectors.toSet()));
        return dto;
    }
    
    // Helper classes
    private static class UserBehaviorProfile {
        List<ReadingHistory> readingHistory;
        Set<String> interests;
        double engagementLevel;
        double averageReadTime;
        int preferredContentLength;
    }
    
    private static class ScoredArticle {
        Article article;
        double score;
        
        ScoredArticle(Article article, double score) {
            this.article = article;
            this.score = score;
        }
    }
} 