package org.example.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "claps", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"article_id", "user_id"})
})
public class Clap {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "clap_count", nullable = false)
    private Integer clapCount = 1;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public Clap() {}
    
    public Clap(Article article, User user) {
        this.article = article;
        this.user = user;
    }
    
    public Clap(Article article, User user, Integer clapCount) {
        this.article = article;
        this.user = user;
        this.clapCount = clapCount;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getClapCount() {
        return clapCount;
    }
    
    public void setClapCount(Integer clapCount) {
        this.clapCount = clapCount;
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
    
    public Article getArticle() {
        return article;
    }
    
    public void setArticle(Article article) {
        this.article = article;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    // Helper methods
    public void incrementClaps() {
        this.clapCount++;
    }
    
    public void decrementClaps() {
        if (this.clapCount > 1) {
            this.clapCount--;
        }
    }
    
    public void setClaps(Integer claps) {
        this.clapCount = Math.max(1, Math.min(50, claps)); // Limit between 1-50 claps
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clap clap = (Clap) o;
        return article.equals(clap.article) && user.equals(clap.user);
    }
    
    @Override
    public int hashCode() {
        return article.hashCode() + user.hashCode();
    }
} 