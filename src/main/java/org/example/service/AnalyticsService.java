package org.example.service;

import org.example.dto.ArticleAnalyticsDto;
import org.example.dto.ReadingHistoryDto;
import org.example.entity.*;
import org.example.repository.*;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.example.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    
    private final ReadingHistoryRepository readingHistoryRepository;
    
    private final ArticleRepository articleRepository;
    
    private final UserRepository userRepository;
    
    private final ClapRepository clapRepository;
    
    private final BookmarkRepository bookmarkRepository;
    
    private final CommentRepository commentRepository;
    
    public List<ReadingHistoryDto> getUserReadingHistory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        List<ReadingHistory> history = readingHistoryRepository.findByUserOrderByReadAtDesc(user);
        
        return history.stream()
                .map(this::toReadingHistoryDto)
                .collect(Collectors.toList());
    }
    
    public ArticleAnalyticsDto getArticleAnalytics(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        
        // Get basic metrics
        Long viewCount = article.getViewCount();
        Long clapCount = clapRepository.getTotalClapsForArticle(article);
        Long commentCount = article.getCommentCount();
        Long bookmarkCount = bookmarkRepository.getBookmarkCountForArticle(article.getId());
        
        // Get share count (placeholder - would need to implement share tracking)
        Long shareCount = 0L; // TODO: Implement share tracking
        
        // Get last viewed time
        List<ReadingHistory> readingHistory = readingHistoryRepository.findByArticleOrderByReadAtDesc(article);
        LocalDateTime lastViewed = readingHistory.isEmpty() ? null : readingHistory.get(0).getReadAt();
        
        // Calculate average read time (placeholder - would need more sophisticated tracking)
        Double averageReadTime = article.getReadTimeMinutes() != null ? 
                article.getReadTimeMinutes().doubleValue() : 0.0;
        
        // Calculate completion rate (placeholder - would need scroll tracking)
        Double completionRate = 0.75; // Placeholder
        
        // Get reader demographics (placeholder - would need user profile data)
        Map<String, Long> readerDemographics = Map.of("Unknown", (long) readingHistory.size());
        
        return new ArticleAnalyticsDto(
                article.getId(),
                article.getTitle(),
                article.getSlug(),
                viewCount,
                clapCount,
                commentCount,
                bookmarkCount,
                shareCount,
                averageReadTime,
                completionRate,
                readerDemographics,
                lastViewed,
                article.getPublishedAt()
        );
    }
    
    public List<ArticleAnalyticsDto> getAuthorAnalytics(String authorUsername) {
        User author = userRepository.findByUsername(authorUsername)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        
        // Get all articles by the author
        List<Article> articles = articleRepository.findByAuthor(authorUsername, 
                org.springframework.data.domain.PageRequest.of(0, 100)).getContent();
        
        return articles.stream()
                .map(article -> getArticleAnalytics(article.getId()))
                .collect(Collectors.toList());
    }
    
    private ReadingHistoryDto toReadingHistoryDto(ReadingHistory history) {
        Article article = history.getArticle();
        User author = article.getAuthor();
        
        return new ReadingHistoryDto(
                history.getId(),
                article.getId(),
                article.getTitle(),
                article.getSlug(),
                author.getUsername(),
                history.getReadAt()
        );
    }
} 