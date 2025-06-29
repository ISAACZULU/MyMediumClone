package org.example.repository;

import org.example.entity.Clap;
import org.example.entity.Article;
import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClapRepository extends JpaRepository<Clap, Long> {
    Optional<Clap> findByArticleAndUser(Article article, User user);
    
    List<Clap> findByUser(User user);
    
    @Query("SELECT SUM(c.clapCount) FROM Clap c WHERE c.article = :article")
    Long getTotalClapsForArticle(@Param("article") Article article);
    
    @Query("SELECT COUNT(c) FROM Clap c WHERE c.article = :article")
    Long getClapperCountForArticle(@Param("article") Article article);
} 