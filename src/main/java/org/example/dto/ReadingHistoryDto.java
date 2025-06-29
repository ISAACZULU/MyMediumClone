package org.example.dto;

import java.time.LocalDateTime;

public class ReadingHistoryDto {
    private Long id;
    private Long articleId;
    private String articleTitle;
    private String articleSlug;
    private String authorUsername;
    private LocalDateTime readAt;
    
    public ReadingHistoryDto() {}
    
    public ReadingHistoryDto(Long id, Long articleId, String articleTitle, String articleSlug, String authorUsername, LocalDateTime readAt) {
        this.id = id;
        this.articleId = articleId;
        this.articleTitle = articleTitle;
        this.articleSlug = articleSlug;
        this.authorUsername = authorUsername;
        this.readAt = readAt;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getArticleId() { return articleId; }
    public void setArticleId(Long articleId) { this.articleId = articleId; }
    public String getArticleTitle() { return articleTitle; }
    public void setArticleTitle(String articleTitle) { this.articleTitle = articleTitle; }
    public String getArticleSlug() { return articleSlug; }
    public void setArticleSlug(String articleSlug) { this.articleSlug = articleSlug; }
    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
} 