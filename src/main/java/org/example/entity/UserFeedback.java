package org.example.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_feedback")
public class UserFeedback {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackType feedbackType;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private String context; // e.g., "recommendation", "search", "feed"
    
    @Column
    private Integer rating; // 1-5 rating if applicable
    
    public enum FeedbackType {
        CLICK,      // User clicked on article
        READ,       // User read the article
        CLAP,       // User clapped
        BOOKMARK,   // User bookmarked
        SHARE,      // User shared
        SKIP,       // User skipped/ignored
        RATE        // User gave explicit rating
    }
    
    public UserFeedback() {}
    
    public UserFeedback(User user, Article article, FeedbackType feedbackType, String context) {
        this.user = user;
        this.article = article;
        this.feedbackType = feedbackType;
        this.context = context;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Article getArticle() { return article; }
    public void setArticle(Article article) { this.article = article; }
    public FeedbackType getFeedbackType() { return feedbackType; }
    public void setFeedbackType(FeedbackType feedbackType) { this.feedbackType = feedbackType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
} 