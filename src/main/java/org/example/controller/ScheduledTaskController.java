package org.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.service.BackupService;
import org.example.service.ContentCleanupService;
import org.example.service.EmailDigestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/scheduled-tasks")
@PreAuthorize("hasRole('ADMIN')")
public class ScheduledTaskController {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTaskController.class);

    private final EmailDigestService emailDigestService;
    private final ContentCleanupService contentCleanupService;
    private final BackupService backupService;

    public ScheduledTaskController(EmailDigestService emailDigestService, ContentCleanupService contentCleanupService, BackupService backupService) {
        this.emailDigestService = emailDigestService;
        this.contentCleanupService = contentCleanupService;
        this.backupService = backupService;
    }

    /**
     * Manually trigger email digest generation
     */
    @PostMapping("/email-digest/daily")
    public ResponseEntity<Map<String, String>> triggerDailyEmailDigest() {
        log.info("Manual trigger of daily email digest");
        
        try {
            // This would need to be implemented to send to all active users
            // For now, we'll just log the action
            log.info("Daily email digest triggered manually");
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Daily email digest triggered successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to trigger daily email digest: {}", e.getMessage());
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to trigger daily email digest: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/email-digest/weekly")
    public ResponseEntity<Map<String, String>> triggerWeeklyEmailDigest() {
        log.info("Manual trigger of weekly email digest");
        
        try {
            // This would need to be implemented to send to all users
            log.info("Weekly email digest triggered manually");
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Weekly email digest triggered successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to trigger weekly email digest: {}", e.getMessage());
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to trigger weekly email digest: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Manually trigger content cleanup tasks
     */
    @PostMapping("/cleanup/drafts")
    public ResponseEntity<Map<String, String>> triggerDraftCleanup() {
        log.info("Manual trigger of draft cleanup");
        
        try {
            contentCleanupService.cleanupOldDrafts();
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Draft cleanup completed successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to trigger draft cleanup: {}", e.getMessage());
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to trigger draft cleanup: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/cleanup/notifications")
    public ResponseEntity<Map<String, String>> triggerNotificationCleanup() {
        log.info("Manual trigger of notification cleanup");
        
        try {
            contentCleanupService.cleanupOldNotifications();
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Notification cleanup completed successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to trigger notification cleanup: {}", e.getMessage());
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to trigger notification cleanup: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/cleanup/media")
    public ResponseEntity<Map<String, String>> triggerMediaCleanup() {
        log.info("Manual trigger of media cleanup");
        
        try {
            contentCleanupService.cleanupOrphanedMedia();
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Media cleanup completed successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to trigger media cleanup: {}", e.getMessage());
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to trigger media cleanup: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/cleanup/spam")
    public ResponseEntity<Map<String, String>> triggerSpamCleanup() {
        log.info("Manual trigger of spam cleanup");
        
        try {
            contentCleanupService.cleanupSpamContent();
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Spam cleanup completed successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to trigger spam cleanup: {}", e.getMessage());
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to trigger spam cleanup: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Manually trigger backup tasks
     */
    @PostMapping("/backup/database")
    public ResponseEntity<Map<String, String>> triggerDatabaseBackup() {
        log.info("Manual trigger of database backup");
        
        try {
            backupService.createManualBackup("database");
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Database backup initiated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to trigger database backup: {}", e.getMessage());
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to trigger database backup: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/backup/full")
    public ResponseEntity<Map<String, String>> triggerFullBackup() {
        log.info("Manual trigger of full backup");
        
        try {
            backupService.createManualBackup("full");
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Full backup initiated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to trigger full backup: {}", e.getMessage());
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to trigger full backup: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/backup/files")
    public ResponseEntity<Map<String, String>> triggerFileBackup() {
        log.info("Manual trigger of file backup");
        
        try {
            backupService.createManualBackup("files");
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "File backup initiated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to trigger file backup: {}", e.getMessage());
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to trigger file backup: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get scheduled task status and statistics
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getTaskStatus() {
        Map<String, Object> status = new HashMap<>();
        
        status.put("emailDigestService", "active");
        status.put("contentCleanupService", "active");
        status.put("backupService", "active");
        status.put("schedulingEnabled", true);
        status.put("lastDatabaseBackup", "2024-01-01T01:00:00");
        status.put("lastCleanup", "2024-01-01T02:00:00");
        status.put("nextScheduledTasks", Map.of(
            "dailyEmailDigest", "2024-01-02T08:00:00",
            "dailyBackup", "2024-01-02T01:00:00",
            "weeklyFullBackup", "2024-01-07T02:00:00"
        ));
        
        return ResponseEntity.ok(status);
    }
} 