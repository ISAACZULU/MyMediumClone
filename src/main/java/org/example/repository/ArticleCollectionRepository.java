package org.example.repository;

import org.example.entity.ArticleCollection;
import org.example.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleCollectionRepository extends JpaRepository<ArticleCollection, Long> {
    List<ArticleCollection> findByOwner(User owner);
    
    Page<ArticleCollection> findByOwner(User owner, Pageable pageable);
    
    List<ArticleCollection> findByOwnerAndIsPublicTrue(User owner);
    
    Page<ArticleCollection> findByIsPublicTrue(Pageable pageable);
    
    @Query("SELECT ac FROM ArticleCollection ac JOIN ac.collaborators c WHERE c = :user")
    List<ArticleCollection> findByCollaborator(@Param("user") User user);
    
    @Query("SELECT ac FROM ArticleCollection ac WHERE ac.isPublic = true AND SIZE(ac.articles) > 0 ORDER BY SIZE(ac.articles) DESC")
    Page<ArticleCollection> findPopularCollections(Pageable pageable);
    
    @Query("SELECT ac FROM ArticleCollection ac JOIN ac.tags t WHERE t.name IN :tags AND ac.isPublic = true")
    List<ArticleCollection> findByTags(@Param("tags") List<String> tags);
} 