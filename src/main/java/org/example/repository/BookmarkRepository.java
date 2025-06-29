package org.example.repository;

import org.example.entity.Bookmark;
import org.example.entity.Article;
import org.example.entity.User;
import org.example.entity.BookmarkCollection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByArticleAndUser(Article article, User user);
    
    Optional<Bookmark> findByArticleIdAndUser(Long articleId, User user);
    
    List<Bookmark> findByUser(User user);
    
    Page<Bookmark> findByUser(User user, Pageable pageable);
    
    List<Bookmark> findByCollection(BookmarkCollection collection);
    
    Page<Bookmark> findByCollection(BookmarkCollection collection, Pageable pageable);
    
    Page<Bookmark> findByArticleId(Long articleId, Pageable pageable);
    
    @Query("SELECT COUNT(b) FROM Bookmark b WHERE b.article.id = :articleId")
    Long getBookmarkCountForArticle(@Param("articleId") Long articleId);
    
    @Query("SELECT b FROM Bookmark b WHERE b.user = :user AND b.collection IS NULL")
    List<Bookmark> findUnorganizedBookmarksByUser(@Param("user") User user);
} 