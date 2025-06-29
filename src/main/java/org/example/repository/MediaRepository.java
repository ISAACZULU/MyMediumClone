package org.example.repository;

import org.example.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
    
    List<Media> findByUserIdOrderByUploadedAtDesc(Long userId);
    
    Optional<Media> findByHash(String hash);
    
    @Query("SELECT m FROM Media m WHERE m.user.id = :userId AND m.processed = true ORDER BY m.uploadedAt DESC")
    List<Media> findProcessedByUserId(@Param("userId") Long userId);
    
    @Query("SELECT m FROM Media m WHERE m.contentType LIKE 'image%' AND m.processed = true ORDER BY m.uploadedAt DESC")
    List<Media> findRecentImages();
    
    @Query("SELECT COUNT(m) FROM Media m WHERE m.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(m.fileSize) FROM Media m WHERE m.user.id = :userId")
    Long getTotalStorageUsedByUserId(@Param("userId") Long userId);
    
    @Query("SELECT m FROM Media m WHERE m.id NOT IN (SELECT DISTINCT a.coverImageUrl FROM Article a WHERE a.coverImageUrl IS NOT NULL) AND m.id NOT IN (SELECT DISTINCT d.coverImageUrl FROM Draft d WHERE d.coverImageUrl IS NOT NULL)")
    List<Media> findOrphanedMedia();
    
    @Query("SELECT m FROM Media m WHERE m.temporary = true AND m.uploadedAt < :dateTime")
    List<Media> findTemporaryMediaOlderThan(@Param("dateTime") java.time.LocalDateTime dateTime);
} 