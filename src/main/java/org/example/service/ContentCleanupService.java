package org.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.entity.Article;
import org.example.entity.Draft;
import org.example.entity.Media;
import org.example.entity.Notification;
import org.example.repository.ArticleRepository;
import org.example.repository.DraftRepository;
import org.example.repository.MediaRepository;
import org.example.repository.NotificationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContentCleanupService {

    private static final Logger log = LoggerFactory.getLogger(ContentCleanupService.class);

    private final DraftRepository draftRepository;
    private final NotificationRepository notificationRepository;
    private final MediaRepository mediaRepository;
    private final ArticleRepository articleRepository;
    private final CloudinaryService cloudinaryService;

    public ContentCleanupService(DraftRepository draftRepository, NotificationRepository notificationRepository, MediaRepository mediaRepository, ArticleRepository articleRepository, CloudinaryService cloudinaryService) {
        this.draftRepository = draftRepository;
        this.notificationRepository = notificationRepository;
        this.mediaRepository = mediaRepository;
        this.articleRepository = articleRepository;
        this.cloudinaryService = cloudinaryService;
    }

    /**
     * Clean up old drafts that haven't been updated in 30 days
     * Runs every day at 2:00 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanupOldDrafts() {
        log.info("Starting old draft cleanup");
        
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Draft> oldDrafts = draftRepository.findByLastModifiedBefore(thirtyDaysAgo);
        
        int deletedCount = 0;
        for (Draft draft : oldDrafts) {
            try {
                // Delete associated media files
                if (draft.getCoverImageUrl() != null) {
                    cloudinaryService.deleteImage(draft.getCoverImageUrl());
                }
                
                draftRepository.delete(draft);
                deletedCount++;
                
                log.debug("Deleted old draft: {}", draft.getId());
            } catch (Exception e) {
                log.error("Failed to delete draft {}: {}", draft.getId(), e.getMessage());
            }
        }
        
        log.info("Completed old draft cleanup. Deleted {} drafts", deletedCount);
    }

    /**
     * Clean up old notifications (older than 90 days)
     * Runs every day at 3:00 AM
     */
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanupOldNotifications() {
        log.info("Starting old notification cleanup");
        
        LocalDateTime ninetyDaysAgo = LocalDateTime.now().minusDays(90);
        List<Notification> oldNotifications = notificationRepository.findByCreatedAtBefore(ninetyDaysAgo);
        
        int deletedCount = 0;
        for (Notification notification : oldNotifications) {
            try {
                notificationRepository.delete(notification);
                deletedCount++;
            } catch (Exception e) {
                log.error("Failed to delete notification {}: {}", notification.getId(), e.getMessage());
            }
        }
        
        log.info("Completed old notification cleanup. Deleted {} notifications", deletedCount);
    }

    /**
     * Clean up orphaned media files
     * Runs every Sunday at 4:00 AM
     */
    @Scheduled(cron = "0 0 4 ? * SUN")
    @Transactional
    public void cleanupOrphanedMedia() {
        log.info("Starting orphaned media cleanup");
        
        // Find media files that are not referenced by any article or draft
        List<Media> orphanedMedia = mediaRepository.findOrphanedMedia();
        
        int deletedCount = 0;
        for (Media media : orphanedMedia) {
            try {
                // Delete from Cloudinary
                cloudinaryService.deleteImage(media.getUrl());
                
                // Delete from database
                mediaRepository.delete(media);
                deletedCount++;
                
                log.debug("Deleted orphaned media: {}", media.getUrl());
            } catch (Exception e) {
                log.error("Failed to delete orphaned media {}: {}", media.getId(), e.getMessage());
            }
        }
        
        log.info("Completed orphaned media cleanup. Deleted {} files", deletedCount);
    }

    /**
     * Archive old articles (older than 2 years) that have low engagement
     * Runs every month on the first day at 5:00 AM
     */
    @Scheduled(cron = "0 0 5 1 * ?")
    @Transactional
    public void archiveOldLowEngagementArticles() {
        log.info("Starting old article archiving");
        
        LocalDateTime twoYearsAgo = LocalDateTime.now().minusYears(2);
        
        // Find articles older than 2 years with less than 10 claps and 5 comments
        List<Article> oldLowEngagementArticles = articleRepository
                .findOldLowEngagementArticles(twoYearsAgo, 10, 5);
        
        int archivedCount = 0;
        for (Article article : oldLowEngagementArticles) {
            try {
                // Mark as archived (soft delete)
                article.setArchived(true);
                article.setArchivedAt(LocalDateTime.now());
                articleRepository.save(article);
                
                archivedCount++;
                log.debug("Archived old article: {}", article.getTitle());
            } catch (Exception e) {
                log.error("Failed to archive article {}: {}", article.getId(), e.getMessage());
            }
        }
        
        log.info("Completed old article archiving. Archived {} articles", archivedCount);
    }

    /**
     * Clean up expired temporary content (drafts, temporary uploads)
     * Runs every 6 hours
     */
    @Scheduled(fixedRate = 6 * 60 * 60 * 1000) // 6 hours in milliseconds
    @Transactional
    public void cleanupTemporaryContent() {
        log.info("Starting temporary content cleanup");
        
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        
        // Clean up temporary media files
        List<Media> temporaryMedia = mediaRepository.findTemporaryMediaOlderThan(oneDayAgo);
        
        int deletedCount = 0;
        for (Media media : temporaryMedia) {
            try {
                cloudinaryService.deleteImage(media.getUrl());
                mediaRepository.delete(media);
                deletedCount++;
            } catch (Exception e) {
                log.error("Failed to delete temporary media {}: {}", media.getId(), e.getMessage());
            }
        }
        
        log.info("Completed temporary content cleanup. Deleted {} temporary files", deletedCount);
    }

    /**
     * Optimize database by cleaning up old reading history records
     * Runs every month on the 15th at 6:00 AM
     */
    @Scheduled(cron = "0 0 6 15 * ?")
    @Transactional
    public void cleanupOldReadingHistory() {
        log.info("Starting old reading history cleanup");
        
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        
        // Keep only the last 100 reading history records per user
        // This is a more complex operation that might need to be done in batches
        // For now, we'll just log that this should be implemented
        
        log.info("Reading history cleanup - keeping last 100 records per user older than 1 year");
        // TODO: Implement batch deletion of old reading history records
    }

    /**
     * Clean up spam and flagged content
     * Runs every day at 7:00 AM
     */
    @Scheduled(cron = "0 0 7 * * ?")
    @Transactional
    public void cleanupSpamContent() {
        log.info("Starting spam content cleanup");
        
        // Find articles with high spam reports
        List<Article> spamArticles = articleRepository.findArticlesWithHighSpamReports(5); // 5+ reports
        
        int cleanedCount = 0;
        for (Article article : spamArticles) {
            try {
                // Mark as spam and hide from public view
                article.setSpam(true);
                article.setSpamAt(LocalDateTime.now());
                articleRepository.save(article);
                
                cleanedCount++;
                log.debug("Marked article as spam: {}", article.getTitle());
            } catch (Exception e) {
                log.error("Failed to mark article as spam {}: {}", article.getId(), e.getMessage());
            }
        }
        
        log.info("Completed spam content cleanup. Marked {} articles as spam", cleanedCount);
    }
} 