package org.example.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BookmarkCollectionDto {
    
    private Long id;
    private String name;
    private String description;
    private boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserProfileDto owner;
    private int bookmarkCount;
    private List<ArticleResponseDto> bookmarks;
    
    // Additional fields for bookmark data
    private Long bookmarkId;
    private Long articleId;
    private Long userId;
    
    // Constructors
    public BookmarkCollectionDto() {}
    
    public BookmarkCollectionDto(String name, String description, boolean isPublic) {
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isPublic() {
        return isPublic;
    }
    
    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public UserProfileDto getOwner() {
        return owner;
    }
    
    public void setOwner(UserProfileDto owner) {
        this.owner = owner;
    }
    
    public int getBookmarkCount() {
        return bookmarkCount;
    }
    
    public void setBookmarkCount(int bookmarkCount) {
        this.bookmarkCount = bookmarkCount;
    }
    
    public List<ArticleResponseDto> getBookmarks() {
        return bookmarks;
    }
    
    public void setBookmarks(List<ArticleResponseDto> bookmarks) {
        this.bookmarks = bookmarks;
    }
    
    public Long getBookmarkId() {
        return bookmarkId;
    }
    
    public void setBookmarkId(Long bookmarkId) {
        this.bookmarkId = bookmarkId;
    }
    
    public Long getArticleId() {
        return articleId;
    }
    
    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
} 