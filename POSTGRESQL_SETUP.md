# PostgreSQL Setup for Medium Clone

## Quick Setup

### 1. Install PostgreSQL
- **Windows**: Download from https://www.postgresql.org/download/windows/
- **macOS**: `brew install postgresql && brew services start postgresql`
- **Linux**: `sudo apt install postgresql postgresql-contrib`

### 2. Create Database
```sql
CREATE DATABASE mediumclone;
```

### 3. Update Configuration
The `application.properties` is already configured for PostgreSQL:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mediumclone
spring.datasource.username=postgres
spring.datup.database.password=password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

### 4. Update Credentials
Replace `password` with your actual PostgreSQL password.

### 5. Run Application
```bash
mvn spring-boot:run
```

The application will automatically create all tables and indexes.

## Backup Configuration
PostgreSQL backups use `pg_dump`:
```bash
pg_dump -h localhost -p 5432 -U postgres -d mediumclone -f backup.sql
```

## Overview

This guide will help you set up PostgreSQL as the database for your Medium Clone backend application.

## Prerequisites

- PostgreSQL 12 or higher installed
- Java 17 or higher
- Maven 3.6 or higher

## 1. Install PostgreSQL

### Windows
1. Download PostgreSQL from: https://www.postgresql.org/download/windows/
2. Run the installer and follow the setup wizard
3. Remember the password you set for the `postgres` user
4. Default port is 5432

### macOS
```bash
# Using Homebrew
brew install postgresql
brew services start postgresql

# Or using the official installer
# Download from: https://www.postgresql.org/download/macosx/
```

### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

## 2. Create Database and User

### Connect to PostgreSQL
```bash
# Connect as postgres user
sudo -u postgres psql

# Or on Windows, use pgAdmin or psql from the PostgreSQL bin directory
```

### Create Database and User
```sql
-- Create database
CREATE DATABASE mediumclone;

-- Create user (optional - you can use postgres user)
CREATE USER mediumclone_user WITH PASSWORD 'your_secure_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE mediumclone TO mediumclone_user;

-- Connect to the database
\c mediumclone

-- Grant schema privileges
GRANT ALL ON SCHEMA public TO mediumclone_user;

-- Exit psql
\q
```

## 3. Update Application Configuration

The application.properties file has already been updated with PostgreSQL configuration:

```properties
# Database Configuration (PostgreSQL)
spring.datasource.url=jdbc:postgresql://localhost:5432/mediumclone
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Connection Pool Configuration
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

### Update Credentials
Replace the default credentials with your actual PostgreSQL credentials:

```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## 4. Environment-Specific Configuration

### Development Environment
Create `application-dev.properties`:
```properties
# Development Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/mediumclone_dev
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

### Production Environment
Create `application-prod.properties`:
```properties
# Production Database Configuration
spring.datasource.url=jdbc:postgresql://your-db-host:5432/mediumclone_prod
spring.datasource.username=mediumclone_user
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Connection Pool for Production
spring.datasource.hikari.maximum-pool-size=50
spring.datasource.hikari.minimum-idle=10
```

## 5. Database Schema

The application will automatically create the database schema when it starts with `spring.jpa.hibernate.ddl-auto=update`.

### Manual Schema Creation (Optional)
If you prefer to create the schema manually, you can:

1. Set `spring.jpa.hibernate.ddl-auto=validate`
2. Run the application once with `create-drop` to generate the schema
3. Copy the generated SQL from the logs
4. Execute the SQL manually in PostgreSQL

## 6. Backup Configuration

The backup service has been updated to use PostgreSQL's `pg_dump` utility:

```properties
# Backup Configuration
app.backup.database.url=jdbc:postgresql://localhost:5432/mediumclone
app.backup.database.username=postgres
app.backup.database.password=password
app.backup.storage.path=./backups
app.backup.retention.days=30
```

### Manual Backup
```bash
# Create a backup
pg_dump -h localhost -p 5432 -U postgres -d mediumclone -f backup.sql

# Restore from backup
psql -h localhost -p 5432 -U postgres -d mediumclone -f backup.sql
```

## 7. Performance Optimization

### Indexes
The application will create necessary indexes automatically. For production, consider adding custom indexes:

```sql
-- Example custom indexes for better performance
CREATE INDEX idx_articles_published_at ON articles(published_at DESC);
CREATE INDEX idx_articles_author_id ON articles(author_id);
CREATE INDEX idx_comments_article_id ON comments(article_id);
CREATE INDEX idx_claps_article_id ON claps(article_id);
CREATE INDEX idx_bookmarks_user_id ON bookmarks(user_id);
```

### PostgreSQL Configuration
Update `postgresql.conf` for better performance:

```conf
# Memory settings
shared_buffers = 256MB
effective_cache_size = 1GB
work_mem = 4MB
maintenance_work_mem = 64MB

# WAL settings
wal_buffers = 16MB
checkpoint_completion_target = 0.9

# Connection settings
max_connections = 100
```

## 8. Security Considerations

### Network Security
```sql
-- Restrict connections to localhost only (development)
-- In pg_hba.conf:
local   all             all                                     md5
host    all             all             127.0.0.1/32            md5
host    all             all             ::1/128                 md5
```

### User Permissions
```sql
-- Create a read-only user for analytics
CREATE USER analytics_user WITH PASSWORD 'analytics_password';
GRANT CONNECT ON DATABASE mediumclone TO analytics_user;
GRANT USAGE ON SCHEMA public TO analytics_user;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO analytics_user;
```

## 9. Monitoring and Maintenance

### Database Size
```sql
-- Check database size
SELECT pg_size_pretty(pg_database_size('mediumclone'));

-- Check table sizes
SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as size
FROM pg_tables 
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

### Connection Monitoring
```sql
-- Check active connections
SELECT 
    pid,
    usename,
    application_name,
    client_addr,
    state,
    query_start
FROM pg_stat_activity 
WHERE datname = 'mediumclone';
```

## 10. Troubleshooting

### Connection Issues
1. Check if PostgreSQL is running:
   ```bash
   sudo systemctl status postgresql
   ```

2. Verify connection:
   ```bash
   psql -h localhost -p 5432 -U postgres -d mediumclone
   ```

3. Check logs:
   ```bash
   sudo tail -f /var/log/postgresql/postgresql-*.log
   ```

### Performance Issues
1. Check slow queries:
   ```sql
   SELECT query, mean_time, calls 
   FROM pg_stat_statements 
   ORDER BY mean_time DESC 
   LIMIT 10;
   ```

2. Analyze table statistics:
   ```sql
   ANALYZE;
   ```

### Backup Issues
1. Ensure `pg_dump` is in your PATH
2. Check file permissions for backup directory
3. Verify PostgreSQL user has backup privileges

## 11. Migration from H2

If you're migrating from H2 to PostgreSQL:

1. Export data from H2:
   ```bash
   java -cp h2.jar org.h2.tools.Script -url jdbc:h2:./data/mediumclone -user sa -script export.sql
   ```

2. Import to PostgreSQL:
   ```bash
   psql -h localhost -p 5432 -U postgres -d mediumclone -f export.sql
   ```

3. Update application configuration
4. Test thoroughly before deploying

## 12. Docker Setup (Optional)

If you prefer using Docker:

```yaml
# docker-compose.yml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: mediumclone
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql

volumes:
  postgres_data:
```

## 13. Next Steps

1. Start the application and verify database connection
2. Check that all tables are created correctly
3. Test the backup functionality
4. Monitor performance and adjust configuration as needed
5. Set up regular maintenance tasks

## Support

For PostgreSQL-specific issues:
- PostgreSQL Documentation: https://www.postgresql.org/docs/
- Stack Overflow: https://stackoverflow.com/questions/tagged/postgresql
- PostgreSQL Community: https://www.postgresql.org/community/ 