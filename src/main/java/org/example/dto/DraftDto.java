package org.example.dto;

import java.time.LocalDateTime;
import java.util.Set;

public class DraftDto {
    
    private Long id;
    private String title;
    private String content;
    private String summary;
    private String coverImageUrl;
    private LocalDateTime autoSavedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserProfileDto author;
    private Set<String> tags;
    private boolean archived;
    private String wordCount;
    private boolean hasContent;
    
    // Constructors
    public DraftDto() {}
    
    public DraftDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public String getCoverImageUrl() {
        return coverImageUrl;
    }
    
    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }
    
    public LocalDateTime getAutoSavedAt() {
        return autoSavedAt;
    }
    
    public void setAutoSavedAt(LocalDateTime autoSavedAt) {
        this.autoSavedAt = autoSavedAt;
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
    
    public UserProfileDto getAuthor() {
        return author;
    }
    
    public void setAuthor(UserProfileDto author) {
        this.author = author;
    }
    
    public Set<String> getTags() {
        return tags;
    }
    
    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
    
    public boolean isArchived() {
        return archived;
    }
    
    public void setArchived(boolean archived) {
        this.archived = archived;
    }
    
    public String getWordCount() {
        return wordCount;
    }
    
    public void setWordCount(String wordCount) {
        this.wordCount = wordCount;
    }
    
    public boolean isHasContent() {
        return hasContent;
    }
    
    public void setHasContent(boolean hasContent) {
        this.hasContent = hasContent;
    }
} 