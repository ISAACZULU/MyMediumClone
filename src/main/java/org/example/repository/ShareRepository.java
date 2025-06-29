package org.example.repository;

import org.example.entity.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ShareRepository extends JpaRepository<Share, Long> {
    
    List<Share> findByArticleId(Long articleId);
    
    List<Share> findByUserId(Long userId);
    
    @Query("SELECT s FROM Share s WHERE s.article.id = :articleId AND s.shareType = :shareType")
    List<Share> findByArticleIdAndShareType(@Param("articleId") Long articleId, 
                                           @Param("shareType") Share.ShareType shareType);
    
    @Query("SELECT COUNT(s) FROM Share s WHERE s.article.id = :articleId")
    Long countByArticleId(@Param("articleId") Long articleId);
    
    @Query("SELECT s.shareType, COUNT(s) FROM Share s WHERE s.article.id = :articleId GROUP BY s.shareType")
    List<Object[]> getShareStatsByArticleId(@Param("articleId") Long articleId);
} 