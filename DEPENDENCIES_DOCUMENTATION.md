# Dependencies Documentation

## Overview
This document provides comprehensive documentation for all dependencies used in the Medium Clone application, including their purposes, versions, and usage.

## Project Information
- **Java Version**: 17
- **Spring Boot Version**: 3.1.5
- **Build Tool**: Maven
- **Packaging**: JAR

---

## Core Dependencies

### 1. Spring Boot Starters

#### 1.1 Spring Boot Starter Web
- **GroupId**: `org.springframework.boot`
- **ArtifactId**: `spring-boot-starter-web`
- **Version**: 3.1.5 (managed by Spring Boot parent)
- **Purpose**: Provides web application support including embedded Tomcat server
- **Key Features**:
  - Embedded Tomcat server
  - Spring MVC for REST API development
  - JSON serialization/deserialization
  - Web security integration
- **Usage**: Core dependency for web application functionality

#### 1.2 Spring Boot Starter Data JPA
- **GroupId**: `org.springframework.boot`
- **ArtifactId**: `spring-boot-starter-data-jpa`
- **Version**: 3.1.5 (managed by Spring Boot parent)
- **Purpose**: Provides JPA and Hibernate support for database operations
- **Key Features**:
  - Hibernate ORM integration
  - Spring Data JPA repositories
  - Database connection pooling (HikariCP)
  - Entity management
- **Usage**: Database persistence layer

#### 1.3 Spring Boot Starter Security
- **GroupId**: `org.springframework.boot`
- **ArtifactId**: `spring-boot-starter-security`
- **Version**: 3.1.5 (managed by Spring Boot parent)
- **Purpose**: Provides security framework for authentication and authorization
- **Key Features**:
  - JWT token authentication
  - Password encryption
  - Role-based access control
  - CSRF protection
- **Usage**: Application security and authentication

#### 1.4 Spring Boot Starter Validation
- **GroupId**: `org.springframework.boot`
- **ArtifactId**: `spring-boot-starter-validation`
- **Version**: 3.1.5 (managed by Spring Boot parent)
- **Purpose**: Provides bean validation support
- **Key Features**:
  - Input validation annotations
  - Custom validation rules
  - Error message handling
- **Usage**: Request data validation

#### 1.5 Spring Boot Starter Data Elasticsearch
- **GroupId**: `org.springframework.boot`
- **ArtifactId**: `spring-boot-starter-data-elasticsearch`
- **Version**: 3.1.5 (managed by Spring Boot parent)
- **Purpose**: Provides Elasticsearch integration for search functionality
- **Key Features**:
  - Elasticsearch client integration
  - Spring Data Elasticsearch repositories
  - Search query building
- **Usage**: Full-text search capabilities

#### 1.6 Spring Boot Starter Actuator
- **GroupId**: `org.springframework.boot`
- **ArtifactId**: `spring-boot-starter-actuator`
- **Version**: 3.1.5 (managed by Spring Boot parent)
- **Purpose**: Provides monitoring and management endpoints
- **Key Features**:
  - Health check endpoints
  - Metrics collection
  - Application monitoring
  - Rate limiting support
- **Usage**: Application monitoring and health checks

#### 1.7 Spring Boot Starter Cache
- **GroupId**: `org.springframework.boot`
- **ArtifactId**: `spring-boot-starter-cache`
- **Version**: 3.1.5 (managed by Spring Boot parent)
- **Purpose**: Provides caching support
- **Key Features**:
  - Caffeine cache integration
  - Cache annotations
  - Cache management
- **Usage**: Performance optimization through caching

#### 1.8 Spring Boot Starter Test
- **GroupId**: `org.springframework.boot`
- **ArtifactId**: `spring-boot-starter-test`
- **Version**: 3.1.5 (managed by Spring Boot parent)
- **Scope**: test
- **Purpose**: Provides testing support
- **Key Features**:
  - JUnit 5 integration
  - Mockito for mocking
  - Test containers support
  - Spring Boot test utilities
- **Usage**: Unit and integration testing

---

## Database Dependencies

### 2.1 H2 Database
- **GroupId**: `com.h2database`
- **ArtifactId**: `h2`
- **Version**: 2.1.214
- **Scope**: runtime
- **Purpose**: In-memory database for development and testing
- **Key Features**:
  - In-memory database
  - H2 console for database management
  - SQL compatibility
- **Usage**: Development and testing database

### 2.2 PostgreSQL
- **GroupId**: `org.postgresql`
- **ArtifactId**: `postgresql`
- **Version**: 42.6.0
- **Scope**: runtime
- **Purpose**: Production database support
- **Key Features**:
  - PostgreSQL JDBC driver
  - Connection pooling support
  - Advanced SQL features
- **Usage**: Production database

---

## Security Dependencies

### 3.1 JWT (JSON Web Tokens)
- **GroupId**: `io.jsonwebtoken`
- **ArtifactId**: `jjwt-api`
- **Version**: 0.11.5
- **Purpose**: JWT token creation and validation
- **Key Features**:
  - JWT token generation
  - Token validation
  - Claims management
- **Usage**: Authentication token management

- **GroupId**: `io.jsonwebtoken`
- **ArtifactId**: `jjwt-impl`
- **Version**: 0.11.5
- **Scope**: runtime
- **Purpose**: JWT implementation

- **GroupId**: `io.jsonwebtoken`
- **ArtifactId**: `jjwt-jackson`
- **Version**: 0.11.5
- **Scope**: runtime
- **Purpose**: JWT Jackson serialization

---

## Media and File Processing

### 4.1 Apache Commons IO
- **GroupId**: `commons-io`
- **ArtifactId**: `commons-io`
- **Version**: 2.11.0
- **Purpose**: File and I/O utilities
- **Key Features**:
  - File operations
  - I/O utilities
  - Stream handling
- **Usage**: File upload and processing

### 4.2 Thumbnailator
- **GroupId**: `net.coobird`
- **ArtifactId**: `thumbnailator`
- **Version**: 0.4.19
- **Purpose**: Image thumbnail generation
- **Key Features**:
  - Image resizing
  - Thumbnail generation
  - Image format conversion
- **Usage**: Image processing and optimization

### 4.3 Cloudinary SDK
- **GroupId**: `com.cloudinary`
- **ArtifactId**: `cloudinary-http44`
- **Version**: 1.35.0
- **Purpose**: Cloudinary cloud storage integration
- **Key Features**:
  - Cloud storage upload
  - Image transformation
  - CDN integration
- **Usage**: Cloud image storage and delivery

---

## API Documentation

### 5.1 SpringDoc OpenAPI
- **GroupId**: `org.springdoc`
- **ArtifactId**: `springdoc-openapi-starter-webmvc-ui`
- **Version**: 2.2.0
- **Purpose**: OpenAPI/Swagger documentation
- **Key Features**:
  - API documentation generation
  - Swagger UI integration
  - OpenAPI 3.0 support
- **Usage**: API documentation and testing interface

---

## Development Tools

### 6.1 Lombok
- **GroupId**: `org.projectlombok`
- **ArtifactId**: `lombok`
- **Version**: 1.18.30
- **Scope**: provided
- **Purpose**: Reduces boilerplate code
- **Key Features**:
  - Automatic getter/setter generation
  - Constructor generation
  - Builder pattern support
  - Logging annotations
- **Usage**: Code generation and simplification

---

## Testing Dependencies

### 7.1 Spring Security Test
- **GroupId**: `org.springframework.security`
- **ArtifactId**: `spring-security-test`
- **Version**: 6.1.5
- **Scope**: test
- **Purpose**: Security testing support
- **Key Features**:
  - Security context testing
  - Authentication testing
  - Authorization testing
- **Usage**: Security-related testing

---

## Jackson Dependencies

### 8.1 Jackson Annotations
- **GroupId**: `com.fasterxml.jackson.core`
- **ArtifactId**: `jackson-annotations`
- **Version**: 2.15.3 (managed by Spring Boot)
- **Purpose**: Jackson JSON annotations
- **Key Features**:
  - JSON serialization annotations
  - Property mapping
  - View filtering
- **Usage**: JSON serialization configuration

---

## Transitive Dependencies

### Key Transitive Dependencies (Managed by Spring Boot)

#### Hibernate ORM
- **Version**: 6.2.13.Final
- **Purpose**: JPA implementation
- **Usage**: Database ORM

#### HikariCP
- **Version**: 5.0.1
- **Purpose**: Database connection pooling
- **Usage**: Database connection management

#### Logback
- **Version**: 1.4.11
- **Purpose**: Logging framework
- **Usage**: Application logging

#### Tomcat Embed
- **Version**: 10.1.15
- **Purpose**: Embedded web server
- **Usage**: HTTP request handling

#### Spring Framework
- **Version**: 6.0.13
- **Purpose**: Core Spring framework
- **Usage**: Dependency injection and core functionality

---

## Dependency Management

### Spring Boot Parent
The project uses Spring Boot parent for dependency management:
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.1.5</version>
</parent>
```

### Java Version
```xml
<properties>
    <java.version>17</java.version>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
</properties>
```

---

## Build Plugins

### 1. Maven Compiler Plugin
- **Version**: 3.13.0
- **Purpose**: Java compilation
- **Configuration**: Java 17 source and target

### 2. Spring Boot Maven Plugin
- **Version**: 3.1.5
- **Purpose**: Spring Boot application packaging
- **Configuration**: Main class specification and repackaging

---

## Security Considerations

### 1. JWT Security
- Uses secure JWT implementation
- Token expiration management
- Secure token storage

### 2. Password Security
- Spring Security password encoding
- BCrypt hashing algorithm
- Password validation

### 3. Input Validation
- Bean validation annotations
- Request data sanitization
- SQL injection prevention

---

## Performance Optimizations

### 1. Caching
- Caffeine cache for in-memory caching
- Cache annotations for method-level caching
- Configurable cache settings

### 2. Database Optimization
- HikariCP connection pooling
- JPA query optimization
- Database indexing support

### 3. Image Processing
- Thumbnail generation for performance
- Image compression
- CDN integration for media delivery

---

## Monitoring and Health Checks

### 1. Actuator Endpoints
- `/actuator/health` - Application health status
- `/actuator/metrics` - Application metrics
- `/actuator/info` - Application information

### 2. Logging
- Logback for structured logging
- Configurable log levels
- Log file rotation

---

## Development and Testing

### 1. Development Tools
- Lombok for code generation
- H2 database for development
- Spring Boot DevTools (if added)

### 2. Testing Framework
- JUnit 5 for unit testing
- Mockito for mocking
- Spring Boot Test for integration testing

---

## Deployment Considerations

### 1. Database
- H2 for development/testing
- PostgreSQL for production
- Connection pooling configuration

### 2. Media Storage
- Local storage for development
- Cloudinary for production
- CDN integration

### 3. Security
- JWT token management
- CORS configuration
- Rate limiting

---

## Version Compatibility

### Spring Boot 3.1.5 Compatibility
- Java 17+ required
- Jakarta EE 9+ support
- Spring Framework 6.x
- Hibernate 6.x

### Database Compatibility
- PostgreSQL 12+
- H2 2.x
- MySQL 8.0+ (if added)

---

## Future Considerations

### Potential Additions
1. **Redis**: For session management and caching
2. **RabbitMQ**: For message queuing
3. **MongoDB**: For document storage
4. **Docker**: For containerization
5. **Kubernetes**: For orchestration

### Monitoring Enhancements
1. **Micrometer**: For metrics collection
2. **Prometheus**: For monitoring
3. **Grafana**: For visualization

### Security Enhancements
1. **OAuth2**: For third-party authentication
2. **Rate Limiting**: More sophisticated rate limiting
3. **API Gateway**: For API management 