package org.example.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "article_analytics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleAnalytics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    @JsonBackReference
    private Article article;
    
    @Column(name = "views_count", nullable = false)
    private Long viewsCount = 0L;
    
    @Column(name = "unique_views_count", nullable = false)
    private Long uniqueViewsCount = 0L;
    
    @Column(name = "avg_reading_time_seconds")
    private Integer avgReadingTimeSeconds = 0;
    
    @Column(name = "bounce_rate")
    private Double bounceRate = 0.0;
    
    @Column(name = "engagement_rate")
    private Double engagementRate = 0.0;
    
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;
    
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
} 