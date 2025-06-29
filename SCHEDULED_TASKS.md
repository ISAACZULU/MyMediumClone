# Scheduled Tasks Documentation

This document outlines all the automated scheduled tasks implemented in the Medium Clone backend system.

## Overview

The system uses Spring's `@Scheduled` annotation to run automated tasks at specified intervals. All scheduled tasks are configured through the `SchedulingConfig` class and run in a dedicated thread pool.

## Task Categories

### 1. Email Digests (`EmailDigestService`)

#### Daily Email Digests
- **Schedule**: Every day at 8:00 AM
- **Cron Expression**: `0 0 8 * * ?`
- **Purpose**: Send personalized daily reading digests to active users
- **Features**:
  - Recent reading history from yesterday
  - Personalized article recommendations
  - Trending articles in user's preferred categories
  - Reading time statistics

#### Weekly Email Digests
- **Schedule**: Every Sunday at 9:00 AM
- **Cron Expression**: `0 0 9 ? * SUN`
- **Purpose**: Send comprehensive weekly summaries to all users
- **Features**:
  - Weekly reading statistics
  - Engagement metrics
  - Top articles of the week
  - Extended personalized recommendations

### 2. Content Cleanup (`ContentCleanupService`)

#### Old Draft Cleanup
- **Schedule**: Every day at 2:00 AM
- **Cron Expression**: `0 0 2 * * ?`
- **Purpose**: Remove drafts that haven't been updated in 30 days
- **Actions**:
  - Delete drafts older than 30 days
  - Remove associated media files from Cloudinary
  - Clean up database records

#### Old Notification Cleanup
- **Schedule**: Every day at 3:00 AM
- **Cron Expression**: `0 0 3 * * ?`
- **Purpose**: Remove notifications older than 90 days
- **Actions**:
  - Delete notifications older than 90 days
  - Maintain database performance

#### Orphaned Media Cleanup
- **Schedule**: Every Sunday at 4:00 AM
- **Cron Expression**: `0 0 4 ? * SUN`
- **Purpose**: Remove media files not referenced by articles or drafts
- **Actions**:
  - Find orphaned media files
  - Delete from Cloudinary CDN
  - Remove database records

#### Old Article Archiving
- **Schedule**: First day of every month at 5:00 AM
- **Cron Expression**: `0 0 5 1 * ?`
- **Purpose**: Archive old articles with low engagement
- **Criteria**:
  - Articles older than 2 years
  - Less than 10 claps
  - Less than 5 comments
- **Actions**:
  - Mark articles as archived (soft delete)
  - Set archived timestamp

#### Temporary Content Cleanup
- **Schedule**: Every 6 hours
- **Fixed Rate**: 6 hours
- **Purpose**: Remove expired temporary content
- **Actions**:
  - Delete temporary media files older than 1 day
  - Clean up temporary uploads

#### Reading History Cleanup
- **Schedule**: 15th of every month at 6:00 AM
- **Cron Expression**: `0 0 6 15 * ?`
- **Purpose**: Optimize database by cleaning old reading history
- **Actions**:
  - Keep only last 100 reading history records per user
  - Remove records older than 1 year

#### Spam Content Cleanup
- **Schedule**: Every day at 7:00 AM
- **Cron Expression**: `0 0 7 * * ?`
- **Purpose**: Handle spam and flagged content
- **Criteria**:
  - Articles with 5+ spam reports
- **Actions**:
  - Mark articles as spam
  - Hide from public view
  - Set spam timestamp

### 3. Backup Routines (`BackupService`)

#### Daily Database Backup
- **Schedule**: Every day at 1:00 AM
- **Cron Expression**: `0 0 1 * * ?`
- **Purpose**: Create daily database backups
- **Features**:
  - Database dump with timestamp
  - Compression to save space
  - Integrity verification
  - Stored in `./backups/database/`

#### Weekly Full Backup
- **Schedule**: Every Sunday at 2:00 AM
- **Cron Expression**: `0 0 2 ? * SUN`
- **Purpose**: Create comprehensive system backups
- **Features**:
  - Database backup
  - File system backup
  - Configuration files
  - Log files
  - Asynchronous execution
  - Stored in `./backups/full/`

#### Incremental File Backup
- **Schedule**: Every 6 hours
- **Fixed Rate**: 6 hours
- **Purpose**: Backup uploaded files incrementally
- **Features**:
  - Track file changes since last backup
  - Delta compression
  - Metadata tracking
  - Stored in `./backups/files/`

#### Backup Cleanup
- **Schedule**: Every day at 3:00 AM
- **Cron Expression**: `0 0 3 * * ?`
- **Purpose**: Remove old backups based on retention policy
- **Features**:
  - Configurable retention period (default: 30 days)
  - Clean up database, full, and log backups
  - Maintain storage efficiency

#### Backup Integrity Verification
- **Schedule**: Every day at 4:00 AM
- **Cron Expression**: `0 0 4 * * ?`
- **Purpose**: Verify backup file integrity
- **Features**:
  - Check recent database backups (last 7 days)
  - Verify recent full backups (last 4 weeks)
  - Checksum verification
  - Test restore capabilities

## Configuration

### Scheduling Configuration
```java
@Configuration
@EnableScheduling
public class SchedulingConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // Configure a thread pool for scheduled tasks
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(10));
    }
}
```

### Application Properties
```properties
# Backup Configuration
app.backup.database.url=jdbc:h2:./data/mediumclone
app.backup.database.username=sa
app.backup.database.password=
app.backup.storage.path=./backups
app.backup.retention.days=30

# Email Configuration (to be implemented)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

## Monitoring and Logging

All scheduled tasks include comprehensive logging:

- **Start/End Logs**: Each task logs when it starts and completes
- **Progress Logs**: Tasks log progress for long-running operations
- **Error Logs**: Detailed error logging with stack traces
- **Statistics**: Task completion statistics (e.g., "Deleted 15 drafts")

## Manual Task Execution

### Email Digests
```java
@Autowired
private EmailDigestService emailDigestService;

// Send daily digest to specific user
emailDigestService.generateAndSendDailyDigest(user);

// Send weekly digest to specific user
emailDigestService.generateAndSendWeeklyDigest(user);
```

### Content Cleanup
```java
@Autowired
private ContentCleanupService contentCleanupService;

// Manual cleanup operations
contentCleanupService.cleanupOldDrafts();
contentCleanupService.cleanupOldNotifications();
contentCleanupService.cleanupOrphanedMedia();
```

### Backup Operations
```java
@Autowired
private BackupService backupService;

// Manual backup creation
backupService.createManualBackup("database");
backupService.createManualBackup("full");
backupService.createManualBackup("files");
```

## Best Practices

### 1. Error Handling
- All tasks include try-catch blocks
- Individual item failures don't stop the entire task
- Comprehensive error logging

### 2. Performance
- Long-running tasks use batch processing
- Database operations are transactional
- File operations are optimized

### 3. Resource Management
- Proper cleanup of temporary files
- Database connection management
- Memory-efficient processing

### 4. Monitoring
- Task execution metrics
- Performance monitoring
- Alert system for failures

## Security Considerations

### 1. Backup Security
- Backup files should be encrypted in production
- Secure storage locations
- Access control for backup files

### 2. Email Security
- Secure SMTP configuration
- Email content sanitization
- Unsubscribe mechanisms

### 3. Data Privacy
- GDPR compliance for user data
- Data retention policies
- Secure deletion of old data

## Production Deployment

### 1. Environment-Specific Configuration
- Different schedules for development/staging/production
- Environment-specific backup locations
- Production-grade email services

### 2. Monitoring and Alerting
- Task execution monitoring
- Failure alerting
- Performance metrics

### 3. Disaster Recovery
- Backup verification procedures
- Restore testing
- Recovery time objectives (RTO)

## Troubleshooting

### Common Issues

1. **Task Not Running**
   - Check if `@EnableScheduling` is enabled
   - Verify cron expressions
   - Check application logs

2. **Database Connection Issues**
   - Verify database connectivity
   - Check connection pool settings
   - Monitor database performance

3. **File System Issues**
   - Check disk space
   - Verify file permissions
   - Monitor I/O performance

4. **Email Delivery Issues**
   - Verify SMTP configuration
   - Check email service quotas
   - Monitor email delivery rates

### Debug Mode
Enable debug logging for scheduled tasks:
```properties
logging.level.org.example.service.EmailDigestService=DEBUG
logging.level.org.example.service.ContentCleanupService=DEBUG
logging.level.org.example.service.BackupService=DEBUG
``` 