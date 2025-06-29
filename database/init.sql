-- Medium Clone Database Initialization Script
-- PostgreSQL

-- Create database (run this as superuser)
-- CREATE DATABASE mediumclone;

-- Connect to the database
-- \c mediumclone;

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Create indexes for better performance
-- (These will be created automatically by JPA, but you can add custom ones here)

-- Example custom indexes for better performance
-- CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_articles_published_at ON articles(published_at DESC);
-- CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_articles_author_id ON articles(author_id);
-- CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_articles_slug ON articles(slug);
-- CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_comments_article_id ON comments(article_id);
-- CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_claps_article_id ON claps(article_id);
-- CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_bookmarks_user_id ON bookmarks(user_id);
-- CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_users_username ON users(username);
-- CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_users_email ON users(email);

-- Create full-text search indexes
-- CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_articles_content_fts ON articles USING gin(to_tsvector('english', content));
-- CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_articles_title_fts ON articles USING gin(to_tsvector('english', title));

-- Create trigram indexes for fuzzy search
-- CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_articles_title_trgm ON articles USING gin(title gin_trgm_ops);
-- CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_users_username_trgm ON users USING gin(username gin_trgm_ops);

-- Grant permissions (if using a separate user)
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO mediumclone_user;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO mediumclone_user;
-- GRANT ALL PRIVILEGES ON SCHEMA public TO mediumclone_user;

-- Note: The actual table creation will be handled by JPA/Hibernate
-- This script is for additional setup and optimization 