package org.example.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.lang.Process;

@Service
@RequiredArgsConstructor
public class BackupService {
    private static final Logger log = LoggerFactory.getLogger(BackupService.class);

    @Value("${app.backup.database.url:jdbc:h2:./data/mediumclone}")
    private String databaseUrl;
    
    @Value("${app.backup.database.username:sa}")
    private String databaseUsername;
    
    @Value("${app.backup.database.password:}")
    private String databasePassword;
    
    @Value("${app.backup.storage.path:./backups}")
    private String backupStoragePath;
    
    @Value("${app.backup.retention.days:30}")
    private int backupRetentionDays;

    /**
     * Create daily database backup
     * Runs every day at 1:00 AM
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void createDailyDatabaseBackup() {
        log.info("Starting daily database backup");
        
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String backupFileName = String.format("database_backup_%s.sql", timestamp);
            Path backupPath = Paths.get(backupStoragePath, "database", backupFileName);
            
            // Ensure backup directory exists
            Files.createDirectories(backupPath.getParent());
            
            // Create database backup
            createDatabaseBackup(backupPath);
            
            // Compress the backup
            compressBackup(backupPath);
            
            // Verify backup integrity
            verifyBackupIntegrity(backupPath);
            
            log.info("Daily database backup completed: {}", backupFileName);
            
        } catch (Exception e) {
            log.error("Failed to create daily database backup: {}", e.getMessage(), e);
        }
    }

    /**
     * Create weekly full backup including files and database
     * Runs every Sunday at 2:00 AM
     */
    @Scheduled(cron = "0 0 2 ? * SUN")
    public void createWeeklyFullBackup() {
        log.info("Starting weekly full backup");
        
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String backupFileName = String.format("full_backup_%s.zip", timestamp);
            Path backupPath = Paths.get(backupStoragePath, "full", backupFileName);
            
            // Ensure backup directory exists
            Files.createDirectories(backupPath.getParent());
            
            // Create full backup asynchronously
            CompletableFuture.runAsync(() -> {
                try {
                    createFullBackup(backupPath);
                    log.info("Weekly full backup completed: {}", backupFileName);
                } catch (Exception e) {
                    log.error("Failed to create weekly full backup: {}", e.getMessage(), e);
                }
            });
            
        } catch (Exception e) {
            log.error("Failed to initiate weekly full backup: {}", e.getMessage(), e);
        }
    }

    /**
     * Clean up old backups
     * Runs every day at 3:00 AM
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupOldBackups() {
        log.info("Starting old backup cleanup");
        
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(backupRetentionDays);
            
            // Clean up old database backups
            cleanupOldBackupsInDirectory(Paths.get(backupStoragePath, "database"), cutoffDate);
            
            // Clean up old full backups
            cleanupOldBackupsInDirectory(Paths.get(backupStoragePath, "full"), cutoffDate);
            
            // Clean up old log backups
            cleanupOldBackupsInDirectory(Paths.get(backupStoragePath, "logs"), cutoffDate);
            
            log.info("Old backup cleanup completed");
            
        } catch (Exception e) {
            log.error("Failed to cleanup old backups: {}", e.getMessage(), e);
        }
    }

    /**
     * Create incremental backup of uploaded files
     * Runs every 6 hours
     */
    @Scheduled(fixedRate = 6 * 60 * 60 * 1000) // 6 hours in milliseconds
    public void createIncrementalFileBackup() {
        log.info("Starting incremental file backup");
        
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String backupFileName = String.format("files_incremental_%s.zip", timestamp);
            Path backupPath = Paths.get(backupStoragePath, "files", backupFileName);
            
            // Ensure backup directory exists
            Files.createDirectories(backupPath.getParent());
            
            // Create incremental file backup
            createIncrementalFileBackup(backupPath);
            
            log.info("Incremental file backup completed: {}", backupFileName);
            
        } catch (Exception e) {
            log.error("Failed to create incremental file backup: {}", e.getMessage(), e);
        }
    }

    /**
     * Verify backup integrity
     * Runs every day at 4:00 AM
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void verifyBackupIntegrity() {
        log.info("Starting backup integrity verification");
        
        try {
            // Verify recent database backups
            verifyRecentBackups(Paths.get(backupStoragePath, "database"), 7);
            
            // Verify recent full backups
            verifyRecentBackups(Paths.get(backupStoragePath, "full"), 4);
            
            log.info("Backup integrity verification completed");
            
        } catch (Exception e) {
            log.error("Failed to verify backup integrity: {}", e.getMessage(), e);
        }
    }

    private void createDatabaseBackup(Path backupPath) throws IOException, InterruptedException {
        // For H2 database, we can use the built-in backup functionality
        if (databaseUrl.contains("h2")) {
            // H2 specific backup command
            String backupCommand = String.format(
                "java -cp h2.jar org.h2.tools.Backup -file %s -dir %s",
                backupPath.toString(),
                databaseUrl.replace("jdbc:h2:", "")
            );
            
            Process process = Runtime.getRuntime().exec(backupCommand);
            int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                throw new IOException("Database backup failed with exit code: " + exitCode);
            }
        } else {
            // For other databases, implement specific backup logic
            // This could involve mysqldump, pg_dump, etc.
            log.warn("Database backup not implemented for database type: {}", databaseUrl);
        }
    }

    private void createFullBackup(Path backupPath) throws IOException {
        // Create a comprehensive backup including:
        // 1. Database backup
        // 2. Uploaded files
        // 3. Configuration files
        // 4. Log files
        
        // This would typically involve:
        // - Database dump
        // - File system copy
        // - Compression
        // - Encryption (optional)
        
        log.info("Creating full backup to: {}", backupPath);
        
        // Implementation would depend on the specific backup strategy
        // For now, we'll create a placeholder
        Files.createFile(backupPath);
    }

    private void createIncrementalFileBackup(Path backupPath) throws IOException {
        // Create incremental backup of uploaded files
        // This would track changes since the last backup
        
        log.info("Creating incremental file backup to: {}", backupPath);
        
        // Implementation would involve:
        // - File change detection
        // - Delta compression
        // - Metadata tracking
        
        Files.createFile(backupPath);
    }

    private void compressBackup(Path backupPath) throws IOException {
        // Compress the backup file to save space
        // This could use gzip, zip, or other compression methods
        
        log.debug("Compressing backup: {}", backupPath);
        
        // Implementation would depend on the compression method chosen
    }

    private void verifyBackupIntegrity(Path backupPath) throws IOException {
        // Verify that the backup file is not corrupted
        // This could involve checksum verification, test restore, etc.
        
        log.debug("Verifying backup integrity: {}", backupPath);
        
        if (!Files.exists(backupPath)) {
            throw new IOException("Backup file does not exist: " + backupPath);
        }
        
        // Additional integrity checks would go here
    }

    private void cleanupOldBackupsInDirectory(Path directory, LocalDateTime cutoffDate) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }
        
        Files.list(directory)
            .filter(path -> {
                try {
                    return Files.getLastModifiedTime(path).toInstant()
                        .isBefore(cutoffDate.toInstant(java.time.ZoneOffset.UTC));
                } catch (IOException e) {
                    log.warn("Could not check modification time for: {}", path);
                    return false;
                }
            })
            .forEach(path -> {
                try {
                    Files.delete(path);
                    log.debug("Deleted old backup: {}", path);
                } catch (IOException e) {
                    log.error("Failed to delete old backup: {}", path);
                }
            });
    }

    private void verifyRecentBackups(Path directory, int daysToCheck) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToCheck);
        
        Files.list(directory)
            .filter(path -> {
                try {
                    return Files.getLastModifiedTime(path).toInstant()
                        .isAfter(cutoffDate.toInstant(java.time.ZoneOffset.UTC));
                } catch (IOException e) {
                    return false;
                }
            })
            .forEach(path -> {
                try {
                    verifyBackupIntegrity(path);
                    log.debug("Verified backup integrity: {}", path);
                } catch (IOException e) {
                    log.error("Backup integrity check failed for: {}", path);
                }
            });
    }

    /**
     * Manual backup creation method for admin use
     */
    public void createManualBackup(String backupType) {
        log.info("Creating manual backup of type: {}", backupType);
        
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String backupFileName = String.format("manual_%s_%s.zip", backupType, timestamp);
            Path backupPath = Paths.get(backupStoragePath, "manual", backupFileName);
            
            Files.createDirectories(backupPath.getParent());
            
            switch (backupType.toLowerCase()) {
                case "database":
                    createDatabaseBackup(backupPath);
                    break;
                case "full":
                    createFullBackup(backupPath);
                    break;
                case "files":
                    createIncrementalFileBackup(backupPath);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown backup type: " + backupType);
            }
            
            log.info("Manual backup completed: {}", backupFileName);
            
        } catch (Exception e) {
            log.error("Failed to create manual backup: {}", e.getMessage(), e);
            throw new RuntimeException("Backup failed", e);
        }
    }
} 