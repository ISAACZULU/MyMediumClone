package org.example.repository;

import org.example.entity.Comment;
import org.example.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.article.id = :articleId AND c.parent IS NULL AND (c.createdAt < :createdAt OR (c.createdAt = :createdAt AND c.id < :id)) ORDER BY c.createdAt DESC, c.id DESC")
    List<Comment> findNextPageByArticle(@Param("articleId") Long articleId, @Param("createdAt") java.time.LocalDateTime createdAt, @Param("id") Long id, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.article.id = :articleId AND c.parent IS NULL ORDER BY c.createdAt DESC, c.id DESC")
    List<Comment> findFirstPageByArticle(@Param("articleId") Long articleId, Pageable pageable);
    
    List<Comment> findByAuthor(User author);
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.article.id = :articleId")
    Long countByArticleId(@Param("articleId") Long articleId);
} 