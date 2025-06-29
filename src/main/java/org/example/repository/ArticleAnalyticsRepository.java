package org.example.repository;

import org.example.entity.ArticleAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleAnalyticsRepository extends JpaRepository<ArticleAnalytics, Long> {
    
    @Query("SELECT aa FROM ArticleAnalytics aa WHERE aa.article.id = :articleId")
    Optional<ArticleAnalytics> findByArticleId(@Param("articleId") Long articleId);
    
    @Query("SELECT aa FROM ArticleAnalytics aa WHERE aa.lastUpdated >= :since ORDER BY aa.viewsCount DESC")
    List<ArticleAnalytics> findTopArticlesByViewsSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT aa FROM ArticleAnalytics aa WHERE aa.lastUpdated >= :since ORDER BY aa.engagementRate DESC")
    List<ArticleAnalytics> findTopArticlesByEngagementSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT AVG(aa.avgReadingTimeSeconds) FROM ArticleAnalytics aa WHERE aa.article.id = :articleId")
    Double getAverageReadingTimeForArticle(@Param("articleId") Long articleId);
    
    @Query("SELECT COUNT(aa) FROM ArticleAnalytics aa WHERE aa.viewsCount > :minViews")
    Long countArticlesWithViewsAbove(@Param("minViews") Long minViews);
    
    @Query("UPDATE ArticleAnalytics aa SET aa.avgReadingTimeSeconds = :readingTime WHERE aa.article.id = :articleId")
    void saveReadingTime(@Param("articleId") Long articleId, @Param("readingTime") long readingTime);
} 