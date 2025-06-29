package org.example.repository;

import org.example.entity.Draft;
import org.example.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DraftRepository extends JpaRepository<Draft, Long> {
    List<Draft> findByAuthor(User author);
    
    Page<Draft> findByAuthor(User author, Pageable pageable);
    
    List<Draft> findByAuthorAndArchivedFalse(User author);
    
    List<Draft> findByAuthorAndArchivedTrue(User author);
    
    @Query("SELECT d FROM Draft d WHERE d.author = :author AND d.autoSavedAt >= :since ORDER BY d.autoSavedAt DESC")
    List<Draft> findRecentDrafts(@Param("author") User author, @Param("since") LocalDateTime since);
    
    @Query("SELECT d FROM Draft d WHERE d.author = :author AND (d.title IS NOT NULL OR d.content IS NOT NULL) ORDER BY d.updatedAt DESC")
    List<Draft> findDraftsWithContent(@Param("author") User author);
    
    @Query("SELECT d FROM Draft d WHERE d.author = :author AND d.autoSavedAt < :before")
    List<Draft> findOldDrafts(@Param("author") User author, @Param("before") LocalDateTime before);
    
    @Query("SELECT d FROM Draft d WHERE d.lastModified < :dateTime")
    List<Draft> findByLastModifiedBefore(@Param("dateTime") LocalDateTime dateTime);
} 