package org.example.repository;

import org.example.entity.Article;
import org.example.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findBySlug(String slug);
    
    @Query("SELECT a FROM Article a WHERE a.published = true ORDER BY a.publishedAt DESC")
    Page<Article> findAllPublished(Pageable pageable);

    @Query("SELECT a FROM Article a JOIN a.tags t WHERE t.name IN :tags AND a.published = true")
    Page<Article> findByTags(@Param("tags") Set<String> tags, Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.author.username = :author AND a.published = true")
    Page<Article> findByAuthor(@Param("author") String author, Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.published = true AND a.publishedAt BETWEEN :startDate AND :endDate")
    Page<Article> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query("SELECT a FROM Article a WHERE (LOWER(a.title) LIKE %:keyword% OR LOWER(a.content) LIKE %:keyword%) AND a.published = true")
    Page<Article> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.published = true ORDER BY a.viewCount DESC, a.likeCount DESC, a.commentCount DESC")
    Page<Article> findTrending(Pageable pageable);

    @Query("SELECT a FROM Article a JOIN a.author u WHERE u.id IN :userIds AND a.published = true")
    Page<Article> findFeedByFollowedUsers(@Param("userIds") List<Long> userIds, Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.published = true AND (a.createdAt < :createdAt OR (a.createdAt = :createdAt AND a.id < :id)) ORDER BY a.createdAt DESC, a.id DESC")
    List<Article> findNextPage(@Param("createdAt") java.time.LocalDateTime createdAt, @Param("id") Long id, Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.published = true ORDER BY a.createdAt DESC, a.id DESC")
    List<Article> findFirstPage(Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.published = true AND a.createdAt >= :since ORDER BY a.clapsCount DESC")
    List<Article> findTopArticlesByClapsInLastDays(@Param("since") LocalDateTime since, Pageable pageable);
    
    @Query("SELECT a FROM Article a JOIN a.tags t WHERE t.name IN :tags AND a.published = true AND a.createdAt >= :since ORDER BY a.clapsCount DESC")
    List<Article> findTrendingArticlesByTags(@Param("tags") Set<String> tags, @Param("since") LocalDateTime since, Pageable pageable);
    
    @Query("SELECT a FROM Article a WHERE a.createdAt < :dateTime AND a.clapsCount < :minClaps AND a.commentCount < :minComments AND a.published = true")
    List<Article> findOldLowEngagementArticles(@Param("dateTime") LocalDateTime dateTime, @Param("minClaps") int minClaps, @Param("minComments") int minComments);
    
    @Query("SELECT a FROM Article a WHERE a.reportCount >= :minReports AND a.published = true")
    List<Article> findArticlesWithHighSpamReports(@Param("minReports") int minReports);
} 