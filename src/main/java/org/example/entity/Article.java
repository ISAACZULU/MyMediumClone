package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "articles")
public class Article {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    @Column(nullable = false)
    private String title;
    
    @Column(unique = true, nullable = false)
    private String slug;
    
    @NotBlank(message = "Content is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Size(max = 500, message = "Summary cannot exceed 500 characters")
    @Column(length = 500)
    private String summary;
    
    @Column(name = "cover_image_url")
    private String coverImageUrl;
    
    @Column(name = "read_time_minutes")
    private Integer readTimeMinutes;
    
    @Column(name = "view_count")
    private Long viewCount = 0L;
    
    @Column(name = "like_count")
    private Long likeCount = 0L;
    
    @Column(name = "comment_count")
    private Long commentCount = 0L;
    
    @Column(name = "claps_count")
    private Long clapsCount = 0L;
    
    @Column(name = "report_count")
    private Long reportCount = 0L;
    
    @Column(name = "is_published")
    private boolean published = false;
    
    @Column(name = "is_featured")
    private boolean featured = false;
    
    @Column(name = "is_archived")
    private boolean archived = false;
    
    @Column(name = "is_spam")
    private boolean spam = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    @Column(name = "archived_at")
    private LocalDateTime archivedAt;
    
    @Column(name = "spam_at")
    private LocalDateTime spamAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "article_tags",
        joinColumns = @JoinColumn(name = "article_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();
    
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ArticleVersion> versions = new HashSet<>();
    
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ArticleLike> likes = new HashSet<>();
    
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<Comment> comments = new HashSet<>();
    
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Clap> claps = new HashSet<>();
    
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Bookmark> bookmarks = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (slug == null || slug.isEmpty()) {
            slug = generateSlug(title);
        }
        if (readTimeMinutes == null) {
            readTimeMinutes = calculateReadTime(content);
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (readTimeMinutes == null) {
            readTimeMinutes = calculateReadTime(content);
        }
    }
    
    // Constructors
    public Article() {}
    
    public Article(String title, String content, User author) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.slug = generateSlug(title);
        this.readTimeMinutes = calculateReadTime(content);
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
        if (this.slug == null || this.slug.isEmpty()) {
            this.slug = generateSlug(title);
        }
    }
    
    public String getSlug() {
        return slug;
    }
    
    public void setSlug(String slug) {
        this.slug = slug;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
        this.readTimeMinutes = calculateReadTime(content);
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public String getCoverImageUrl() {
        return coverImageUrl;
    }
    
    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }
    
    public Integer getReadTimeMinutes() {
        return readTimeMinutes;
    }
    
    public void setReadTimeMinutes(Integer readTimeMinutes) {
        this.readTimeMinutes = readTimeMinutes;
    }
    
    public Long getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }
    
    public Long getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }
    
    public Long getCommentCount() {
        return commentCount;
    }
    
    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }
    
    public Long getClapsCount() {
        return clapsCount;
    }
    
    public void setClapsCount(Long clapsCount) {
        this.clapsCount = clapsCount;
    }
    
    public Long getReportCount() {
        return reportCount;
    }
    
    public void setReportCount(Long reportCount) {
        this.reportCount = reportCount;
    }
    
    public boolean isPublished() {
        return published;
    }
    
    public void setPublished(boolean published) {
        this.published = published;
        if (published && this.publishedAt == null) {
            this.publishedAt = LocalDateTime.now();
        }
    }
    
    public boolean isFeatured() {
        return featured;
    }
    
    public void setFeatured(boolean featured) {
        this.featured = featured;
    }
    
    public boolean isArchived() {
        return archived;
    }
    
    public void setArchived(boolean archived) {
        this.archived = archived;
    }
    
    public boolean isSpam() {
        return spam;
    }
    
    public void setSpam(boolean spam) {
        this.spam = spam;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }
    
    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
    
    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }
    
    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }
    
    public LocalDateTime getSpamAt() {
        return spamAt;
    }
    
    public void setSpamAt(LocalDateTime spamAt) {
        this.spamAt = spamAt;
    }
    
    public User getAuthor() {
        return author;
    }
    
    public void setAuthor(User author) {
        this.author = author;
    }
    
    public Set<Tag> getTags() {
        return tags;
    }
    
    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
    
    public Set<ArticleVersion> getVersions() {
        return versions;
    }
    
    public void setVersions(Set<ArticleVersion> versions) {
        this.versions = versions;
    }
    
    public Set<ArticleLike> getLikes() {
        return likes;
    }
    
    public void setLikes(Set<ArticleLike> likes) {
        this.likes = likes;
    }
    
    public Set<Comment> getComments() {
        return comments;
    }
    
    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }
    
    public Set<Clap> getClaps() {
        return claps;
    }
    
    public void setClaps(Set<Clap> claps) {
        this.claps = claps;
    }
    
    public Set<Bookmark> getBookmarks() {
        return bookmarks;
    }
    
    public void setBookmarks(Set<Bookmark> bookmarks) {
        this.bookmarks = bookmarks;
    }
    
    // Helper methods
    public void incrementViewCount() {
        this.viewCount++;
    }
    
    public void incrementLikeCount() {
        this.likeCount++;
    }
    
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
    
    public void incrementCommentCount() {
        this.commentCount++;
    }
    
    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }
    
    private String generateSlug(String title) {
        if (title == null || title.isEmpty()) {
            return "";
        }
        return title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }
    
    private int calculateReadTime(String content) {
        if (content == null || content.isEmpty()) {
            return 1;
        }
        // Average reading speed: 200 words per minute
        int wordCount = content.split("\\s+").length;
        return Math.max(1, (int) Math.ceil(wordCount / 200.0));
    }
} 