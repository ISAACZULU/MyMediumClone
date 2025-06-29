package org.example.repository;

import org.example.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByCreatedAtBefore(LocalDateTime dateTime);
    
    List<Notification> findByRecipientIdAndReadFalseOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = ?1 AND n.createdAt >= ?2")
    List<Notification> findRecentNotificationsByUserId(Long userId, LocalDateTime since);
    
    long countByRecipientIdAndReadFalse(Long userId);
} 