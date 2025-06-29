package org.example.repository;

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

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
    boolean existsByName(String name);
    
    Page<Tag> findByNameContainingIgnoreCase(String query, Pageable pageable);
    
    @Query("SELECT t FROM Tag t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY t.articleCount DESC")
    List<Tag> findByNameContainingIgnoreCaseOrderByArticleCountDesc(@Param("query") String query);
    
    @Query("SELECT t FROM Tag t ORDER BY t.trendingScore DESC, t.articleCount DESC")
    Page<Tag> findTrendingTags(Pageable pageable);
    
    @Query("SELECT t FROM Tag t WHERE t.lastUsed >= :since ORDER BY t.weeklyUsage DESC")
    List<Tag> findRecentlyUsedTags(@Param("since") LocalDateTime since);
    
    @Query("SELECT t FROM Tag t ORDER BY t.articleCount DESC")
    Page<Tag> findMostUsedTags(Pageable pageable);
    
    @Query("SELECT t FROM Tag t WHERE t.articleCount > 0 ORDER BY t.lastUsed DESC")
    List<Tag> findRecentlyActiveTags();
} 