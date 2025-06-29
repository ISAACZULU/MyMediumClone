package org.example.dto;

import java.time.LocalDateTime;

public class MediaResponseDto {
    private Long id;
    private String originalFilename;
    private String contentType;
    private Long fileSize;
    private String url;
    private String thumbnailUrl;
    private Integer width;
    private Integer height;
    private LocalDateTime uploadedAt;
    private boolean processed;
    
    public MediaResponseDto() {}
    
    public MediaResponseDto(Long id, String originalFilename, String contentType, 
                           Long fileSize, String url, String thumbnailUrl, 
                           Integer width, Integer height, LocalDateTime uploadedAt, boolean processed) {
        this.id = id;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.width = width;
        this.height = height;
        this.uploadedAt = uploadedAt;
        this.processed = processed;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOriginalFilename() {
        return originalFilename;
    }
    
    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
    
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
    
    public Integer getWidth() {
        return width;
    }
    
    public void setWidth(Integer width) {
        this.width = width;
    }
    
    public Integer getHeight() {
        return height;
    }
    
    public void setHeight(Integer height) {
        this.height = height;
    }
    
    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
    
    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
    
    public boolean isProcessed() {
        return processed;
    }
    
    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
} 