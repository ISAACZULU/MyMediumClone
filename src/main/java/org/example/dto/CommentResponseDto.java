package org.example.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponseDto {
    
    private Long id;
    private String content;
    private Long likeCount;
    private Long flagCount;
    private boolean flagged;
    private boolean hidden;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserProfileDto author;
    private Long parentId;
    private List<CommentResponseDto> replies;
    private boolean isReply;
    private boolean hasReplies;
    
    // Constructors
    public CommentResponseDto() {}
    
    public CommentResponseDto(Long id, String content, UserProfileDto author) {
        this.id = id;
        this.content = content;
        this.author = author;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Long getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }
    
    public Long getFlagCount() {
        return flagCount;
    }
    
    public void setFlagCount(Long flagCount) {
        this.flagCount = flagCount;
    }
    
    public boolean isFlagged() {
        return flagged;
    }
    
    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }
    
    public boolean isHidden() {
        return hidden;
    }
    
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
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
    
    public Long getParentId() {
        return parentId;
    }
    
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    
    public List<CommentResponseDto> getReplies() {
        return replies;
    }
    
    public void setReplies(List<CommentResponseDto> replies) {
        this.replies = replies;
    }
    
    public boolean isReply() {
        return isReply;
    }
    
    public void setReply(boolean reply) {
        isReply = reply;
    }
    
    public boolean isHasReplies() {
        return hasReplies;
    }
    
    public void setHasReplies(boolean hasReplies) {
        this.hasReplies = hasReplies;
    }
} 