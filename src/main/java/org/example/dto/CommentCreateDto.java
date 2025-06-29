package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentCreateDto {
    
    @NotBlank(message = "Comment content is required")
    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    private String content;
    
    private Long parentId; // For nested comments
    
    // Constructors
    public CommentCreateDto() {}
    
    public CommentCreateDto(String content) {
        this.content = content;
    }
    
    public CommentCreateDto(String content, Long parentId) {
        this.content = content;
        this.parentId = parentId;
    }
    
    // Getters and Setters
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Long getParentId() {
        return parentId;
    }
    
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
} 