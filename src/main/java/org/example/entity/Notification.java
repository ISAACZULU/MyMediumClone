package org.example.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime readAt;
    
    @Column(nullable = false)
    private boolean read = false;
    
    @Column(nullable = false)
    private boolean emailSent = false;
    
    @Column(nullable = false)
    private boolean pushSent = false;
    
    // Related entities (optional, for context)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;
    
    @Column
    private String actionUrl; // URL to navigate to when notification is clicked
    
    @Column
    private String imageUrl; // Optional image for the notification
    
    public enum NotificationType {
        // Article-related notifications
        ARTICLE_PUBLISHED,      // When someone publishes an article
        ARTICLE_LIKED,          // When someone likes your article
        ARTICLE_CLAPPED,        // When someone claps your article
        ARTICLE_BOOKMARKED,     // When someone bookmarks your article
        ARTICLE_SHARED,         // When someone shares your article
        
        // Comment-related notifications
        COMMENT_ADDED,          // When someone comments on your article
        COMMENT_REPLIED,        // When someone replies to your comment
        COMMENT_LIKED,          // When someone likes your comment
        
        // User-related notifications
        USER_FOLLOWED,          // When someone follows you
        USER_MENTIONED,         // When someone mentions you in a comment
        
        // System notifications
        WELCOME,                // Welcome notification for new users
        SYSTEM_UPDATE,          // System updates and announcements
        SECURITY_ALERT,         // Security-related notifications
        
        // Content organization
        COLLECTION_SHARED,      // When someone shares a collection with you
        COLLECTION_UPDATED,     // When a collection you follow is updated
        
        // Moderation
        CONTENT_REPORTED,       // When your content is reported
        CONTENT_APPROVED,       // When your reported content is approved
        CONTENT_REMOVED         // When your content is removed
    }
    
    public Notification() {}
    
    public Notification(User recipient, NotificationType type, String title, String message) {
        this.recipient = recipient;
        this.type = type;
        this.title = title;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }
    
    public Notification(User recipient, User sender, NotificationType type, String title, String message) {
        this.recipient = recipient;
        this.sender = sender;
        this.type = type;
        this.title = title;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getRecipient() { return recipient; }
    public void setRecipient(User recipient) { this.recipient = recipient; }
    
    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }
    
    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
    
    public boolean isRead() { return read; }
    public void setRead(boolean read) { 
        this.read = read; 
        if (read && this.readAt == null) {
            this.readAt = LocalDateTime.now();
        }
    }
    
    public boolean isEmailSent() { return emailSent; }
    public void setEmailSent(boolean emailSent) { this.emailSent = emailSent; }
    
    public boolean isPushSent() { return pushSent; }
    public void setPushSent(boolean pushSent) { this.pushSent = pushSent; }
    
    public Article getArticle() { return article; }
    public void setArticle(Article article) { this.article = article; }
    
    public Comment getComment() { return comment; }
    public void setComment(Comment comment) { this.comment = comment; }
    
    public String getActionUrl() { return actionUrl; }
    public void setActionUrl(String actionUrl) { this.actionUrl = actionUrl; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    // Helper methods
    public void markAsRead() {
        this.read = true;
        this.readAt = LocalDateTime.now();
    }
    
    public boolean isUnread() {
        return !this.read;
    }
} 