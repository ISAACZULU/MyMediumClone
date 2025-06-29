package org.example.repository;

import org.example.entity.ReadingHistory;
import org.example.entity.User;
import org.example.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, Long> {
    List<ReadingHistory> findByUserOrderByReadAtDesc(User user);
    List<ReadingHistory> findByArticleOrderByReadAtDesc(Article article);
    Optional<ReadingHistory> findByUserAndArticle(User user, Article article);
    @Query("SELECT rh FROM ReadingHistory rh WHERE rh.user.id = :userId ORDER BY rh.readAt DESC")
    List<ReadingHistory> findRecentByUserId(@Param("userId") Long userId);
    
    @Query("SELECT rh FROM ReadingHistory rh WHERE rh.user.id = :userId AND rh.readAt > :since ORDER BY rh.readAt DESC")
    List<ReadingHistory> findByUserIdAndReadAtAfterOrderByReadAtDesc(@Param("userId") Long userId, @Param("since") java.time.LocalDateTime since);
    
    @Query("SELECT COUNT(rh) FROM ReadingHistory rh WHERE rh.user.id = :userId AND rh.readAt > :since")
    long countByUserIdAndReadAtAfter(@Param("userId") Long userId, @Param("since") java.time.LocalDateTime since);
} 