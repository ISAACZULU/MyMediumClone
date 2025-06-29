package org.example.dto;

import org.example.entity.Share;

public class ShareDto {
    
    private String shareUrl;
    private String title;
    private String description;
    private String imageUrl;
    private String author;
    private String publishedDate;
    private String readTime;
    private Share.ShareType shareType;
    private String shareMessage;
    
    // Constructors
    public ShareDto() {}
    
    public ShareDto(String shareUrl, String title, String description) {
        this.shareUrl = shareUrl;
        this.title = title;
        this.description = description;
    }
    
    // Getters and Setters
    public String getShareUrl() {
        return shareUrl;
    }
    
    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getPublishedDate() {
        return publishedDate;
    }
    
    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }
    
    public String getReadTime() {
        return readTime;
    }
    
    public void setReadTime(String readTime) {
        this.readTime = readTime;
    }
    
    public Share.ShareType getShareType() {
        return shareType;
    }
    
    public void setShareType(Share.ShareType shareType) {
        this.shareType = shareType;
    }
    
    public String getShareMessage() {
        return shareMessage;
    }
    
    public void setShareMessage(String shareMessage) {
        this.shareMessage = shareMessage;
    }
} 