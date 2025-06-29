package org.example.repository;

import org.example.entity.Report;
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
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    // Find reports by status
    List<Report> findByStatusOrderByCreatedAtDesc(Report.ReportStatus status);
    
    Page<Report> findByStatusOrderByCreatedAtDesc(Report.ReportStatus status, Pageable pageable);
    
    // Find pending reports (for moderation queue)
    List<Report> findByStatusOrderBySeverityDescCreatedAtDesc(Report.ReportStatus status);
    
    Page<Report> findByStatusOrderBySeverityDescCreatedAtDesc(Report.ReportStatus status, Pageable pageable);
    
    // Find reports by type
    List<Report> findByTypeOrderByCreatedAtDesc(Report.ReportType type);
    
    Page<Report> findByTypeOrderByCreatedAtDesc(Report.ReportType type, Pageable pageable);
    
    // Find reports by reporter
    List<Report> findByReporterOrderByCreatedAtDesc(User reporter);
    
    Page<Report> findByReporterOrderByCreatedAtDesc(User reporter, Pageable pageable);
    
    // Find reports by reviewer
    List<Report> findByReviewerOrderByReviewedAtDesc(User reviewer);
    
    Page<Report> findByReviewerOrderByReviewedAtDesc(User reviewer, Pageable pageable);
    
    // Find reports for specific content
    List<Report> findByReportedArticleOrderByCreatedAtDesc(org.example.entity.Article article);
    
    List<Report> findByReportedCommentOrderByCreatedAtDesc(org.example.entity.Comment comment);
    
    List<Report> findByReportedUserOrderByCreatedAtDesc(User reportedUser);
    
    // Find reports by severity
    List<Report> findBySeverityGreaterThanEqualOrderByCreatedAtDesc(Integer severity);
    
    Page<Report> findBySeverityGreaterThanEqualOrderByCreatedAtDesc(Integer severity, Pageable pageable);
    
    // Find reports created within a time range
    List<Report> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);
    
    // Find reports reviewed within a time range
    List<Report> findByReviewedAtBetweenOrderByReviewedAtDesc(LocalDateTime start, LocalDateTime end);
    
    // Count reports by status
    long countByStatus(Report.ReportStatus status);
    
    // Count reports by type
    long countByType(Report.ReportType type);
    
    // Count reports by severity
    long countBySeverityGreaterThanEqual(Integer severity);
    
    // Find reports that need escalation (high severity, pending for too long)
    @Query("SELECT r FROM Report r WHERE r.status = 'PENDING' AND r.severity >= 4 AND r.createdAt < :threshold ORDER BY r.severity DESC, r.createdAt ASC")
    List<Report> findReportsNeedingEscalation(@Param("threshold") LocalDateTime threshold);
    
    // Find duplicate reports (same content, same reporter)
    @Query("SELECT r FROM Report r WHERE r.reporter = :reporter AND r.reportedArticle = :article AND r.createdAt > :since")
    List<Report> findDuplicateArticleReports(@Param("reporter") User reporter, @Param("article") org.example.entity.Article article, @Param("since") LocalDateTime since);
    
    @Query("SELECT r FROM Report r WHERE r.reporter = :reporter AND r.reportedComment = :comment AND r.createdAt > :since")
    List<Report> findDuplicateCommentReports(@Param("reporter") User reporter, @Param("comment") org.example.entity.Comment comment, @Param("since") LocalDateTime since);
    
    // Find reports by multiple statuses
    @Query("SELECT r FROM Report r WHERE r.status IN :statuses ORDER BY r.createdAt DESC")
    List<Report> findByStatusInOrderByCreatedAtDesc(@Param("statuses") List<Report.ReportStatus> statuses);
    
    // Find reports by multiple types
    @Query("SELECT r FROM Report r WHERE r.type IN :types ORDER BY r.createdAt DESC")
    List<Report> findByTypeInOrderByCreatedAtDesc(@Param("types") List<Report.ReportType> types);
    
    // Find anonymous reports
    List<Report> findByAnonymousTrueOrderByCreatedAtDesc();
    
    Page<Report> findByAnonymousTrueOrderByCreatedAtDesc(Pageable pageable);
    
    // Find reports with evidence
    @Query("SELECT r FROM Report r WHERE r.evidence IS NOT NULL AND r.evidence != '' ORDER BY r.createdAt DESC")
    List<Report> findReportsWithEvidence();
    
    // Find reports by entity type
    @Query("SELECT r FROM Report r WHERE r.reportedArticle IS NOT NULL ORDER BY r.createdAt DESC")
    List<Report> findArticleReports();
    
    @Query("SELECT r FROM Report r WHERE r.reportedComment IS NOT NULL ORDER BY r.createdAt DESC")
    List<Report> findCommentReports();
    
    @Query("SELECT r FROM Report r WHERE r.reportedUser IS NOT NULL ORDER BY r.createdAt DESC")
    List<Report> findUserReports();
} 