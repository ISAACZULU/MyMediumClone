# Database Schema Documentation

## Overview
This document describes the complete database schema for the Medium Clone application. The schema is designed to support a full-featured content platform with user management, content creation, engagement, moderation, and analytics.

## Entity Relationships

```
User (1) ←→ (N) Article
User (1) ←→ (N) Comment
User (1) ←→ (N) Clap
User (1) ←→ (N) Bookmark
User (1) ←→ (N) Notification
User (1) ←→ (N) Report
User (1) ←→ (N) ReadingHistory
User (1) ←→ (N) Media
User (1) ←→ (N) Draft
User (1) ←→ (N) ArticleCollection
User (1) ←→ (N) BookmarkCollection
User (1) ←→ (N) UserFeedback

Article (1) ←→ (N) Comment
Article (1) ←→ (N) Clap
Article (1) ←→ (N) Bookmark
Article (1) ←→ (N) ArticleVersion
Article (1) ←→ (N) ReadingHistory
Article (1) ←→ (N) Report
Article (1) ←→ (N) Notification
Article (N) ←→ (N) Tag
Article (N) ←→ (N) ArticleCollection

Comment (1) ←→ (N) Comment (replies)
Comment (1) ←→ (N) Report
Comment (1) ←→ (N) Notification

Tag (N) ←→ (N) Article
Tag (N) ←→ (N) ArticleCollection
```

## Entity Details

### 1. User
**Table**: `users`
**Purpose**: Core user entity for authentication and profile management

**Key Fields**:
- `id` (PK): Unique identifier
- `username`: Unique username
- `email`: Unique email address
- `password_hash`: Encrypted password
- `bio`: User biography
- `profile_image_url`: Profile picture URL
- `created_at`: Account creation timestamp
- `email_verified`: Email verification status
- `reset_token`: Password reset token
- `reset_token_expiry`: Token expiration

**Relationships**:
- One-to-Many: Articles, Comments, Claps, Bookmarks, Notifications, Reports
- Many-to-Many: Followers/Following (self-referencing)

### 2. Article
**Table**: `articles`
**Purpose**: Main content entity for published articles

**Key Fields**:
- `id` (PK): Unique identifier
- `title`: Article title
- `slug`: URL-friendly title
- `content`: Article content (TEXT)
- `summary`: Article summary
- `cover_image_url`: Cover image URL
- `read_time_minutes`: Estimated reading time
- `view_count`: View counter
- `like_count`: Like counter
- `comment_count`: Comment counter
- `is_published`: Publication status
- `is_featured`: Featured status
- `created_at`: Creation timestamp
- `updated_at`: Last update timestamp
- `published_at`: Publication timestamp
- `author_id` (FK): Author reference

**Relationships**:
- Many-to-One: User (author)
- One-to-Many: Comments, Claps, Bookmarks, Versions, ReadingHistory
- Many-to-Many: Tags, ArticleCollections

### 3. Comment
**Table**: `comments`
**Purpose**: User comments on articles with nested replies

**Key Fields**:
- `id` (PK): Unique identifier
- `content`: Comment content (TEXT)
- `like_count`: Like counter
- `flag_count`: Flag counter
- `is_flagged`: Flagged status
- `is_hidden`: Hidden status
- `created_at`: Creation timestamp
- `updated_at`: Last update timestamp
- `article_id` (FK): Article reference
- `author_id` (FK): Author reference
- `parent_id` (FK): Parent comment for replies

**Relationships**:
- Many-to-One: Article, User (author), Comment (parent)
- One-to-Many: Comment (replies), Reports

### 4. Tag
**Table**: `tags`
**Purpose**: Content categorization and discovery

**Key Fields**:
- `id` (PK): Unique identifier
- `name`: Tag name (unique)
- `description`: Tag description
- `article_count`: Number of articles with this tag
- `trending_score`: Trending algorithm score
- `weekly_usage`: Weekly usage count
- `monthly_usage`: Monthly usage count
- `last_used`: Last usage timestamp
- `created_at`: Creation timestamp

**Relationships**:
- Many-to-Many: Articles, ArticleCollections

### 5. Clap
**Table**: `claps`
**Purpose**: User engagement with articles (multiple claps per user)

**Key Fields**:
- `id` (PK): Unique identifier
- `clap_count`: Number of claps (1-50)
- `created_at`: Creation timestamp
- `updated_at`: Last update timestamp
- `article_id` (FK): Article reference
- `user_id` (FK): User reference

**Relationships**:
- Many-to-One: Article, User

### 6. Bookmark
**Table**: `bookmarks`
**Purpose**: User bookmarks for articles

**Key Fields**:
- `id` (PK): Unique identifier
- `created_at`: Creation timestamp
- `article_id` (FK): Article reference
- `user_id` (FK): User reference
- `collection_id` (FK): Bookmark collection reference

**Relationships**:
- Many-to-One: Article, User, BookmarkCollection

### 7. BookmarkCollection
**Table**: `bookmark_collections`
**Purpose**: Organized bookmark collections

**Key Fields**:
- `id` (PK): Unique identifier
- `name`: Collection name
- `description`: Collection description
- `is_public`: Public visibility
- `created_at`: Creation timestamp
- `updated_at`: Last update timestamp
- `owner_id` (FK): Owner reference

**Relationships**:
- Many-to-One: User (owner)
- One-to-Many: Bookmarks

### 8. ArticleCollection
**Table**: `article_collections`
**Purpose**: Curated article collections

**Key Fields**:
- `id` (PK): Unique identifier
- `name`: Collection name
- `description`: Collection description
- `is_public`: Public visibility
- `is_collaborative`: Collaborative editing
- `article_count`: Number of articles
- `created_at`: Creation timestamp
- `updated_at`: Last update timestamp
- `owner_id` (FK): Owner reference

**Relationships**:
- Many-to-One: User (owner)
- Many-to-Many: Articles, Tags, Users (collaborators)

### 9. ArticleVersion
**Table**: `article_versions`
**Purpose**: Version history for articles

**Key Fields**:
- `id` (PK): Unique identifier
- `version_number`: Version number
- `title`: Article title at this version
- `content`: Article content at this version (TEXT)
- `summary`: Article summary at this version
- `cover_image_url`: Cover image URL at this version
- `change_description`: Description of changes
- `created_at`: Creation timestamp
- `article_id` (FK): Article reference
- `created_by_id` (FK): Creator reference

**Relationships**:
- Many-to-One: Article, User (creator)

### 10. Draft
**Table**: `drafts`
**Purpose**: Article drafts with auto-save functionality

**Key Fields**:
- `id` (PK): Unique identifier
- `title`: Draft title
- `content`: Draft content (TEXT)
- `summary`: Draft summary
- `cover_image_url`: Cover image URL
- `is_archived`: Archive status
- `auto_saved_at`: Last auto-save timestamp
- `created_at`: Creation timestamp
- `updated_at`: Last update timestamp
- `author_id` (FK): Author reference

**Relationships**:
- Many-to-One: User (author)

### 11. Media
**Table**: `media`
**Purpose**: File uploads and media management

**Key Fields**:
- `id` (PK): Unique identifier
- `original_filename`: Original file name
- `stored_filename`: Stored file name
- `content_type`: MIME type
- `file_size`: File size in bytes
- `file_path`: File storage path
- `thumbnail_path`: Thumbnail path
- `width`: Image width (if applicable)
- `height`: Image height (if applicable)
- `hash`: File hash for deduplication
- `uploaded_at`: Upload timestamp
- `processed_at`: Processing completion timestamp
- `is_processed`: Processing status
- `cdn_url`: CDN URL
- `user_id` (FK): Uploader reference

**Relationships**:
- Many-to-One: User (uploader)

### 12. ReadingHistory
**Table**: `reading_history`
**Purpose**: Track user reading behavior for analytics

**Key Fields**:
- `id` (PK): Unique identifier
- `read_at`: Reading timestamp
- `user_id` (FK): Reader reference
- `article_id` (FK): Article reference

**Relationships**:
- Many-to-One: User, Article

### 13. UserFeedback
**Table**: `user_feedback`
**Purpose**: Track user interactions for recommendation algorithms

**Key Fields**:
- `id` (PK): Unique identifier
- `feedback_type`: Type of feedback (CLICK, READ, CLAP, etc.)
- `context`: Context of the feedback (recommendation, search, feed)
- `rating`: Optional 1-5 rating
- `created_at`: Creation timestamp
- `user_id` (FK): User reference
- `article_id` (FK): Article reference

**Relationships**:
- Many-to-One: User, Article

### 14. Notification
**Table**: `notifications`
**Purpose**: User notifications for various events

**Key Fields**:
- `id` (PK): Unique identifier
- `type`: Notification type (ARTICLE_LIKED, COMMENT_ADDED, etc.)
- `title`: Notification title
- `message`: Notification message (TEXT)
- `created_at`: Creation timestamp
- `read_at`: Read timestamp
- `is_read`: Read status
- `is_email_sent`: Email delivery status
- `is_push_sent`: Push notification status
- `action_url`: URL to navigate to
- `image_url`: Optional notification image
- `recipient_id` (FK): Recipient reference
- `sender_id` (FK): Sender reference (optional)
- `article_id` (FK): Related article (optional)
- `comment_id` (FK): Related comment (optional)

**Relationships**:
- Many-to-One: User (recipient), User (sender), Article, Comment

### 15. Report
**Table**: `reports`
**Purpose**: Content moderation and user reporting

**Key Fields**:
- `id` (PK): Unique identifier
- `type`: Report type (SPAM, HARASSMENT, etc.)
- `status`: Report status (PENDING, RESOLVED, etc.)
- `reason`: Report reason
- `description`: Detailed description (TEXT)
- `created_at`: Creation timestamp
- `reviewed_at`: Review timestamp
- `moderator_notes`: Moderator notes (TEXT)
- `evidence`: Evidence URLs (TEXT)
- `reported_content`: Snapshot of reported content
- `severity`: Severity level (1-5)
- `is_anonymous`: Anonymous report flag
- `reporter_id` (FK): Reporter reference
- `reviewer_id` (FK): Moderator reference (optional)
- `reported_article_id` (FK): Reported article (optional)
- `reported_comment_id` (FK): Reported comment (optional)
- `reported_user_id` (FK): Reported user (optional)

**Relationships**:
- Many-to-One: User (reporter), User (reviewer), Article, Comment, User (reported)

## Junction Tables

### Article Tags
**Table**: `article_tags`
**Purpose**: Many-to-many relationship between articles and tags

**Fields**:
- `article_id` (FK): Article reference
- `tag_id` (FK): Tag reference

### Article Collections
**Table**: `article_collection_articles`
**Purpose**: Many-to-many relationship between articles and collections

**Fields**:
- `collection_id` (FK): Collection reference
- `article_id` (FK): Article reference

### Collection Tags
**Table**: `article_collection_tags`
**Purpose**: Many-to-many relationship between collections and tags

**Fields**:
- `collection_id` (FK): Collection reference
- `tag_id` (FK): Tag reference

### Collection Collaborators
**Table**: `article_collection_collaborators`
**Purpose**: Many-to-many relationship between collections and collaborators

**Fields**:
- `collection_id` (FK): Collection reference
- `user_id` (FK): User reference

### User Followers
**Table**: `user_followers`
**Purpose**: Many-to-many relationship for user following

**Fields**:
- `follower_id` (FK): Follower reference
- `following_id` (FK): Following reference

## Indexes

### Performance Indexes
```sql
-- User indexes
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Article indexes
CREATE INDEX idx_articles_author_id ON articles(author_id);
CREATE INDEX idx_articles_published ON articles(is_published);
CREATE INDEX idx_articles_published_at ON articles(published_at);
CREATE INDEX idx_articles_slug ON articles(slug);
CREATE INDEX idx_articles_view_count ON articles(view_count);
CREATE INDEX idx_articles_like_count ON articles(like_count);

-- Comment indexes
CREATE INDEX idx_comments_article_id ON comments(article_id);
CREATE INDEX idx_comments_author_id ON comments(author_id);
CREATE INDEX idx_comments_parent_id ON comments(parent_id);
CREATE INDEX idx_comments_created_at ON comments(created_at);

-- Engagement indexes
CREATE INDEX idx_claps_article_user ON claps(article_id, user_id);
CREATE INDEX idx_bookmarks_article_user ON bookmarks(article_id, user_id);
CREATE INDEX idx_reading_history_user_read_at ON reading_history(user_id, read_at);

-- Notification indexes
CREATE INDEX idx_notifications_recipient_read ON notifications(recipient_id, is_read);
CREATE INDEX idx_notifications_recipient_created_at ON notifications(recipient_id, created_at);

-- Report indexes
CREATE INDEX idx_reports_status_created_at ON reports(status, created_at);
CREATE INDEX idx_reports_type ON reports(type);
CREATE INDEX idx_reports_severity ON reports(severity);
```

### Full-Text Search Indexes
```sql
-- Article content search
CREATE FULLTEXT INDEX idx_articles_content_search ON articles(title, content, summary);

-- Comment content search
CREATE FULLTEXT INDEX idx_comments_content_search ON comments(content);

-- User search
CREATE FULLTEXT INDEX idx_users_search ON users(username, bio);
```

## Constraints

### Unique Constraints
```sql
-- User uniqueness
ALTER TABLE users ADD CONSTRAINT uk_users_username UNIQUE (username);
ALTER TABLE users ADD CONSTRAINT uk_users_email UNIQUE (email);

-- Article slug uniqueness
ALTER TABLE users ADD CONSTRAINT uk_articles_slug UNIQUE (slug);

-- Tag name uniqueness
ALTER TABLE tags ADD CONSTRAINT uk_tags_name UNIQUE (name);

-- Reading history uniqueness
ALTER TABLE reading_history ADD CONSTRAINT uk_reading_history_user_article UNIQUE (user_id, article_id);

-- Clap uniqueness
ALTER TABLE claps ADD CONSTRAINT uk_claps_article_user UNIQUE (article_id, user_id);

-- Bookmark uniqueness
ALTER TABLE bookmarks ADD CONSTRAINT uk_bookmarks_article_user UNIQUE (article_id, user_id);
```

### Foreign Key Constraints
All foreign key relationships are properly constrained to maintain referential integrity.

## Data Retention Policies

### Automatic Cleanup
- **Drafts**: Auto-delete after 90 days of inactivity
- **Notifications**: Delete read notifications after 30 days
- **UserFeedback**: Archive after 1 year
- **Reports**: Archive resolved reports after 2 years
- **ReadingHistory**: Archive after 6 months

### Backup Strategy
- Daily incremental backups
- Weekly full backups
- Monthly archival backups
- Point-in-time recovery capability

## Security Considerations

### Data Protection
- All passwords are hashed using BCrypt
- Sensitive data is encrypted at rest
- API keys and tokens are encrypted
- Personal data can be anonymized for GDPR compliance

### Access Control
- Row-level security for user data
- Role-based access control for moderation
- Audit logging for sensitive operations
- Rate limiting on all endpoints 