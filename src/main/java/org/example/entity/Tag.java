package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
public class Tag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Tag name is required")
    @Size(max = 50, message = "Tag name cannot exceed 50 characters")
    @Column(unique = true, nullable = false)
    private String name;
    
    @Size(max = 200, message = "Description cannot exceed 200 characters")
    @Column(length = 200)
    private String description;
    
    @Column(name = "article_count")
    private Long articleCount = 0L;
    
    @Column(name = "weekly_usage")
    private Long weeklyUsage = 0L;
    
    @Column(name = "monthly_usage")
    private Long monthlyUsage = 0L;
    
    @Column(name = "trending_score")
    private Double trendingScore = 0.0;
    
    @Column(name = "last_used")
    private LocalDateTime lastUsed;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<Article> articles = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructors
    public Tag() {}
    
    public Tag(String name) {
        this.name = name;
    }
    
    public Tag(String name, String description) {
        this.name = name;
        this.description = description;
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
    
    public Long getArticleCount() {
        return articleCount;
    }
    
    public void setArticleCount(Long articleCount) {
        this.articleCount = articleCount;
    }
    
    public Long getWeeklyUsage() {
        return weeklyUsage;
    }
    
    public void setWeeklyUsage(Long weeklyUsage) {
        this.weeklyUsage = weeklyUsage;
    }
    
    public Long getMonthlyUsage() {
        return monthlyUsage;
    }
    
    public void setMonthlyUsage(Long monthlyUsage) {
        this.monthlyUsage = monthlyUsage;
    }
    
    public Double getTrendingScore() {
        return trendingScore;
    }
    
    public void setTrendingScore(Double trendingScore) {
        this.trendingScore = trendingScore;
    }
    
    public LocalDateTime getLastUsed() {
        return lastUsed;
    }
    
    public void setLastUsed(LocalDateTime lastUsed) {
        this.lastUsed = lastUsed;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Set<Article> getArticles() {
        return articles;
    }
    
    public void setArticles(Set<Article> articles) {
        this.articles = articles;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return name.equals(tag.name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public String toString() {
        return name;
    }
} 