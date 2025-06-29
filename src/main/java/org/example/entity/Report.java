package org.example.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.PENDING;
    
    @Column(nullable = false)
    private String reason;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime reviewedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;
    
    @Column(columnDefinition = "TEXT")
    private String moderatorNotes;
    
    // Reported content references
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_article_id")
    private Article reportedArticle;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_comment_id")
    private Comment reportedComment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id")
    private User reportedUser;
    
    // Evidence and context
    @Column(columnDefinition = "TEXT")
    private String evidence; // URLs, screenshots, etc.
    
    @Column
    private String reportedContent; // Snapshot of reported content
    
    @Column
    private Integer severity = 1; // 1-5 scale, 5 being most severe
    
    @Column(nullable = false)
    private boolean anonymous = false;
    
    public enum ReportType {
        // Content-related reports
        SPAM,                   // Spam content
        HARASSMENT,             // Harassment or bullying
        HATE_SPEECH,            // Hate speech or discrimination
        VIOLENCE,               // Violent content
        MISINFORMATION,         // False or misleading information
        COPYRIGHT,              // Copyright infringement
        PLAGIARISM,             // Plagiarized content
        INAPPROPRIATE,          // Inappropriate content
        NSFW,                   // Not safe for work content
        
        // User-related reports
        FAKE_ACCOUNT,           // Fake or impersonation account
        BOT_ACCOUNT,            // Bot or automated account
        MULTIPLE_ACCOUNTS,      // Multiple accounts by same user
        
        // Technical reports
        BUG,                    // Technical issues
        BROKEN_LINK,            // Broken links
        MALWARE,                // Malicious content
        
        // Other
        OTHER                   // Other reasons
    }
    
    public enum ReportStatus {
        PENDING,        // Report submitted, waiting for review
        UNDER_REVIEW,   // Report is being reviewed by moderator
        RESOLVED,       // Report resolved (action taken)
        DISMISSED,      // Report dismissed (no action needed)
        ESCALATED,      // Report escalated to higher authority
        CLOSED          // Report closed (no further action)
    }
    
    public Report() {}
    
    public Report(User reporter, ReportType type, String reason, String description) {
        this.reporter = reporter;
        this.type = type;
        this.reason = reason;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getReporter() { return reporter; }
    public void setReporter(User reporter) { this.reporter = reporter; }
    
    public ReportType getType() { return type; }
    public void setType(ReportType type) { this.type = type; }
    
    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
    
    public User getReviewer() { return reviewer; }
    public void setReviewer(User reviewer) { this.reviewer = reviewer; }
    
    public String getModeratorNotes() { return moderatorNotes; }
    public void setModeratorNotes(String moderatorNotes) { this.moderatorNotes = moderatorNotes; }
    
    public Article getReportedArticle() { return reportedArticle; }
    public void setReportedArticle(Article reportedArticle) { this.reportedArticle = reportedArticle; }
    
    public Comment getReportedComment() { return reportedComment; }
    public void setReportedComment(Comment reportedComment) { this.reportedComment = reportedComment; }
    
    public User getReportedUser() { return reportedUser; }
    public void setReportedUser(User reportedUser) { this.reportedUser = reportedUser; }
    
    public String getEvidence() { return evidence; }
    public void setEvidence(String evidence) { this.evidence = evidence; }
    
    public String getReportedContent() { return reportedContent; }
    public void setReportedContent(String reportedContent) { this.reportedContent = reportedContent; }
    
    public Integer getSeverity() { return severity; }
    public void setSeverity(Integer severity) { this.severity = severity; }
    
    public boolean isAnonymous() { return anonymous; }
    public void setAnonymous(boolean anonymous) { this.anonymous = anonymous; }
    
    // Helper methods
    public void markAsReviewed(User reviewer) {
        this.status = ReportStatus.UNDER_REVIEW;
        this.reviewer = reviewer;
        this.reviewedAt = LocalDateTime.now();
    }
    
    public void resolve(String moderatorNotes) {
        this.status = ReportStatus.RESOLVED;
        this.moderatorNotes = moderatorNotes;
        this.reviewedAt = LocalDateTime.now();
    }
    
    public void dismiss(String moderatorNotes) {
        this.status = ReportStatus.DISMISSED;
        this.moderatorNotes = moderatorNotes;
        this.reviewedAt = LocalDateTime.now();
    }
    
    public void escalate() {
        this.status = ReportStatus.ESCALATED;
        this.reviewedAt = LocalDateTime.now();
    }
    
    public boolean isPending() {
        return this.status == ReportStatus.PENDING;
    }
    
    public boolean isUnderReview() {
        return this.status == ReportStatus.UNDER_REVIEW;
    }
    
    public boolean isResolved() {
        return this.status == ReportStatus.RESOLVED;
    }
    
    public Long getReportedEntityId() {
        if (reportedArticle != null) return reportedArticle.getId();
        if (reportedComment != null) return reportedComment.getId();
        if (reportedUser != null) return reportedUser.getId();
        return null;
    }
} 