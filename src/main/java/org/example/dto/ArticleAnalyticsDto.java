package org.example.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class ArticleAnalyticsDto {
    private Long articleId;
    private String articleTitle;
    private String articleSlug;
    private Long viewCount;
    private Long clapCount;
    private Long commentCount;
    private Long bookmarkCount;
    private Long shareCount;
    private Double averageReadTime;
    private Double completionRate;
    private Map<String, Long> readerDemographics; // e.g., {"country": count}
    private LocalDateTime lastViewed;
    private LocalDateTime publishedAt;
    
    public ArticleAnalyticsDto() {}
    
    public ArticleAnalyticsDto(Long articleId, String articleTitle, String articleSlug, 
                              Long viewCount, Long clapCount, Long commentCount, 
                              Long bookmarkCount, Long shareCount, Double averageReadTime, 
                              Double completionRate, Map<String, Long> readerDemographics,
                              LocalDateTime lastViewed, LocalDateTime publishedAt) {
        this.articleId = articleId;
        this.articleTitle = articleTitle;
        this.articleSlug = articleSlug;
        this.viewCount = viewCount;
        this.clapCount = clapCount;
        this.commentCount = commentCount;
        this.bookmarkCount = bookmarkCount;
        this.shareCount = shareCount;
        this.averageReadTime = averageReadTime;
        this.completionRate = completionRate;
        this.readerDemographics = readerDemographics;
        this.lastViewed = lastViewed;
        this.publishedAt = publishedAt;
    }
    
    // Getters and Setters
    public Long getArticleId() { return articleId; }
    public void setArticleId(Long articleId) { this.articleId = articleId; }
    public String getArticleTitle() { return articleTitle; }
    public void setArticleTitle(String articleTitle) { this.articleTitle = articleTitle; }
    public String getArticleSlug() { return articleSlug; }
    public void setArticleSlug(String articleSlug) { this.articleSlug = articleSlug; }
    public Long getViewCount() { return viewCount; }
    public void setViewCount(Long viewCount) { this.viewCount = viewCount; }
    public Long getClapCount() { return clapCount; }
    public void setClapCount(Long clapCount) { this.clapCount = clapCount; }
    public Long getCommentCount() { return commentCount; }
    public void setCommentCount(Long commentCount) { this.commentCount = commentCount; }
    public Long getBookmarkCount() { return bookmarkCount; }
    public void setBookmarkCount(Long bookmarkCount) { this.bookmarkCount = bookmarkCount; }
    public Long getShareCount() { return shareCount; }
    public void setShareCount(Long shareCount) { this.shareCount = shareCount; }
    public Double getAverageReadTime() { return averageReadTime; }
    public void setAverageReadTime(Double averageReadTime) { this.averageReadTime = averageReadTime; }
    public Double getCompletionRate() { return completionRate; }
    public void setCompletionRate(Double completionRate) { this.completionRate = completionRate; }
    public Map<String, Long> getReaderDemographics() { return readerDemographics; }
    public void setReaderDemographics(Map<String, Long> readerDemographics) { this.readerDemographics = readerDemographics; }
    public LocalDateTime getLastViewed() { return lastViewed; }
    public void setLastViewed(LocalDateTime lastViewed) { this.lastViewed = lastViewed; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
} 