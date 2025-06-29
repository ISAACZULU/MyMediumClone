package org.example.service;

import org.example.dto.ArticleResponseDto;
import org.example.entity.*;
import org.example.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    
    @Autowired
    private ArticleRepository articleRepository;
    
    @Autowired
    private ReadingHistoryRepository readingHistoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TagRepository tagRepository;
    
    public List<ArticleResponseDto> getMoreLikeThis(Long articleId, String username, int limit) {
        Article sourceArticle = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        
        // Get tags from the source article
        Set<String> sourceTags = sourceArticle.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
        
        // Find articles with similar tags
        List<Article> similarArticles = articleRepository.findByTags(sourceTags, 
                org.springframework.data.domain.PageRequest.of(0, limit * 2)).getContent();
        
        // Filter out the source article
        List<Article> filteredArticles = similarArticles.stream()
                .filter(article -> !article.getId().equals(articleId))
                .limit(limit)
                .collect(Collectors.toList());
        
        return filteredArticles.stream()
                .map(this::toArticleResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<ArticleResponseDto> getPersonalizedFeed(String username, int limit) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get user's reading history
        List<ReadingHistory> readingHistory = readingHistoryRepository.findByUserOrderByReadAtDesc(user);
        
        // Extract user's interests from reading history
        Set<String> userInterests = extractUserInterests(readingHistory);
        
        // Get articles based on user interests
        List<Article> interestBasedArticles = new ArrayList<>();
        if (!userInterests.isEmpty()) {
            interestBasedArticles = articleRepository.findByTags(userInterests, 
                    org.springframework.data.domain.PageRequest.of(0, limit)).getContent();
        }
        
        // Sort by popularity and recency
        List<Article> sortedArticles = interestBasedArticles.stream()
                .sorted((a1, a2) -> {
                    double score1 = calculateScore(a1);
                    double score2 = calculateScore(a2);
                    return Double.compare(score2, score1);
                })
                .limit(limit)
                .collect(Collectors.toList());
        
        return sortedArticles.stream()
                .map(this::toArticleResponseDto)
                .collect(Collectors.toList());
    }
    
    private Set<String> extractUserInterests(List<ReadingHistory> readingHistory) {
        Set<String> interests = new HashSet<>();
        
        for (ReadingHistory history : readingHistory) {
            Article article = history.getArticle();
            interests.addAll(article.getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toSet()));
        }
        
        return interests;
    }
    
    private double calculateScore(Article article) {
        double score = 0.0;
        
        // Popularity score
        score += Math.log10(article.getViewCount() + 1) * 0.5;
        score += Math.log10(article.getLikeCount() + 1) * 0.3;
        
        // Recency score
        if (article.getPublishedAt() != null) {
            long daysSincePublished = java.time.Duration.between(article.getPublishedAt(), 
                    java.time.LocalDateTime.now()).toDays();
            double recencyScore = Math.max(0.5, 1.0 - (daysSincePublished / 365.0));
            score += recencyScore * 0.2;
        }
        
        return score;
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
} 