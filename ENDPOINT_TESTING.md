# Endpoint Testing Guide

## Overview
This document provides a comprehensive guide for testing all API endpoints in the Medium Clone application. It includes test scripts, expected responses, and troubleshooting tips.

## Prerequisites
- Application running on `http://localhost:8080`
- PowerShell or curl available
- JWT token for authenticated endpoints

---

## 1. Health Check

### Test Application Status
```powershell
# PowerShell
Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method GET

# Expected Response
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    }
  }
}
```

---

## 2. Authentication Endpoints

### 2.1 Register User
```powershell
# PowerShell
$body = @{
    username = "testuser"
    email = "test@example.com"
    password = "password123"
    confirmPassword = "password123"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/register" -Method POST -Body $body -ContentType "application/json"
$response | ConvertTo-Json

# Expected Response
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "User registered successfully",
  "success": true
}
```

### 2.2 Login User
```powershell
# PowerShell
$body = @{
    username = "testuser"
    password = "password123"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/login" -Method POST -Body $body -ContentType "application/json"
$token = $response.token
$token | ConvertTo-Json

# Expected Response
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Login successful",
  "success": true
}
```

### 2.3 Request Password Reset
```powershell
# PowerShell
$body = @{
    email = "test@example.com"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/forgot-password" -Method POST -Body $body -ContentType "application/json"

# Expected Response
{
  "message": "Password reset email sent",
  "success": true
}
```

---

## 3. User Management Endpoints

### 3.1 Get Current User Profile
```powershell
# PowerShell
$headers = @{
    "Authorization" = "Bearer $token"
}

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/users/profile" -Method GET -Headers $headers
$response | ConvertTo-Json

# Expected Response
{
  "id": 1,
  "username": "testuser",
  "email": "test@example.com",
  "bio": null,
  "profileImageUrl": null,
  "followersCount": 0,
  "followingCount": 0,
  "articlesCount": 0
}
```

### 3.2 Update User Profile
```powershell
# PowerShell
$headers = @{
    "Authorization" = "Bearer $token"
}

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/users/profile?bio=Test bio&profileImageUrl=https://example.com/image.jpg" -Method PUT -Headers $headers
$response | ConvertTo-Json

# Expected Response
{
  "id": 1,
  "username": "testuser",
  "email": "test@example.com",
  "bio": "Test bio",
  "profileImageUrl": "https://example.com/image.jpg",
  "followersCount": 0,
  "followingCount": 0,
  "articlesCount": 0
}
```

### 3.3 Get User Profile by Username
```powershell
# PowerShell
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/users/public/testuser" -Method GET
$response | ConvertTo-Json

# Expected Response
{
  "id": 1,
  "username": "testuser",
  "email": "test@example.com",
  "bio": "Test bio",
  "profileImageUrl": "https://example.com/image.jpg",
  "followersCount": 0,
  "followingCount": 0,
  "articlesCount": 0
}
```

---

## 4. Article Management Endpoints

### 4.1 Create Article
```powershell
# PowerShell
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$body = @{
    title = "Test Article"
    content = "This is a test article content with some sample text."
    summary = "A test article summary"
    tags = @("test", "sample")
    published = $true
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/articles" -Method POST -Body $body -Headers $headers
$articleId = $response.id
$response | ConvertTo-Json

# Expected Response
{
  "id": 1,
  "title": "Test Article",
  "content": "This is a test article content with some sample text.",
  "summary": "A test article summary",
  "slug": "test-article",
  "author": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "bio": "Test bio",
    "profileImageUrl": "https://example.com/image.jpg",
    "followersCount": 0,
    "followingCount": 0,
    "articlesCount": 1
  },
  "tags": ["test", "sample"],
  "published": true,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00",
  "clapsCount": 0,
  "commentsCount": 0,
  "readingTime": 1
}
```

### 4.2 Get Article by Slug
```powershell
# PowerShell
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/articles/slug/test-article" -Method GET
$response | ConvertTo-Json

# Expected Response
{
  "id": 1,
  "title": "Test Article",
  "content": "This is a test article content with some sample text.",
  "summary": "A test article summary",
  "slug": "test-article",
  "author": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "bio": "Test bio",
    "profileImageUrl": "https://example.com/image.jpg",
    "followersCount": 0,
    "followingCount": 0,
    "articlesCount": 1
  },
  "tags": ["test", "sample"],
  "published": true,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00",
  "clapsCount": 0,
  "commentsCount": 0,
  "readingTime": 1
}
```

### 4.3 Edit Article
```powershell
# PowerShell
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$body = @{
    title = "Updated Test Article"
    content = "This is an updated test article content."
    summary = "An updated test article summary"
    tags = @("test", "updated", "sample")
    published = $true
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/articles/$articleId" -Method PUT -Body $body -Headers $headers
$response | ConvertTo-Json

# Expected Response
{
  "id": 1,
  "title": "Updated Test Article",
  "content": "This is an updated test article content.",
  "summary": "An updated test article summary",
  "slug": "updated-test-article",
  "author": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "bio": "Test bio",
    "profileImageUrl": "https://example.com/image.jpg",
    "followersCount": 0,
    "followingCount": 0,
    "articlesCount": 1
  },
  "tags": ["test", "updated", "sample"],
  "published": true,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:05:00",
  "clapsCount": 0,
  "commentsCount": 0,
  "readingTime": 1
}
```

### 4.4 Get Trending Articles
```powershell
# PowerShell
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/articles/trending?page=0&size=10" -Method GET
$response | ConvertTo-Json

# Expected Response
{
  "content": [
    {
      "id": 1,
      "title": "Updated Test Article",
      "content": "This is an updated test article content.",
      "summary": "An updated test article summary",
      "slug": "updated-test-article",
      "author": {
        "id": 1,
        "username": "testuser",
        "email": "test@example.com",
        "bio": "Test bio",
        "profileImageUrl": "https://example.com/image.jpg",
        "followersCount": 0,
        "followingCount": 0,
        "articlesCount": 1
      },
      "tags": ["test", "updated", "sample"],
      "published": true,
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:05:00",
      "clapsCount": 0,
      "commentsCount": 0,
      "readingTime": 1
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0,
  "first": true,
  "last": true
}
```

### 4.5 Search Articles
```powershell
# PowerShell
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/articles/search?keyword=test&page=0&size=10" -Method GET
$response | ConvertTo-Json

# Expected Response
{
  "content": [
    {
      "id": 1,
      "title": "Updated Test Article",
      "content": "This is an updated test article content.",
      "summary": "An updated test article summary",
      "slug": "updated-test-article",
      "author": {
        "id": 1,
        "username": "testuser",
        "email": "test@example.com",
        "bio": "Test bio",
        "profileImageUrl": "https://example.com/image.jpg",
        "followersCount": 0,
        "followingCount": 0,
        "articlesCount": 1
      },
      "tags": ["test", "updated", "sample"],
      "published": true,
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:05:00",
      "clapsCount": 0,
      "commentsCount": 0,
      "readingTime": 1
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0,
  "first": true,
  "last": true
}
```

---

## 5. Comment Management Endpoints

### 5.1 Create Comment
```powershell
# PowerShell
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$body = @{
    content = "This is a test comment on the article."
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/comments/article/$articleId" -Method POST -Body $body -Headers $headers
$commentId = $response.id
$response | ConvertTo-Json

# Expected Response
{
  "id": 1,
  "content": "This is a test comment on the article.",
  "author": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "bio": "Test bio",
    "profileImageUrl": "https://example.com/image.jpg",
    "followersCount": 0,
    "followingCount": 0,
    "articlesCount": 1
  },
  "articleId": 1,
  "parentCommentId": null,
  "createdAt": "2024-01-01T10:10:00",
  "updatedAt": "2024-01-01T10:10:00"
}
```

### 5.2 Get Article Comments
```powershell
# PowerShell
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/comments/article/$articleId?page=0&size=10" -Method GET
$response | ConvertTo-Json

# Expected Response
[
  {
    "id": 1,
    "content": "This is a test comment on the article.",
    "author": {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com",
      "bio": "Test bio",
      "profileImageUrl": "https://example.com/image.jpg",
      "followersCount": 0,
      "followingCount": 0,
      "articlesCount": 1
    },
    "articleId": 1,
    "parentCommentId": null,
    "createdAt": "2024-01-01T10:10:00",
    "updatedAt": "2024-01-01T10:10:00"
  }
]
```

### 5.3 Reply to Comment
```powershell
# PowerShell
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$body = @{
    content = "This is a reply to the comment."
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/comments/$commentId/reply" -Method POST -Body $body -Headers $headers
$response | ConvertTo-Json

# Expected Response
{
  "id": 2,
  "content": "This is a reply to the comment.",
  "author": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "bio": "Test bio",
    "profileImageUrl": "https://example.com/image.jpg",
    "followersCount": 0,
    "followingCount": 0,
    "articlesCount": 1
  },
  "articleId": 1,
  "parentCommentId": 1,
  "createdAt": "2024-01-01T10:15:00",
  "updatedAt": "2024-01-01T10:15:00"
}
```

---

## 6. Engagement Endpoints

### 6.1 Clap Article
```powershell
# PowerShell
$headers = @{
    "Authorization" = "Bearer $token"
}

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/engagement/articles/$articleId/clap?clapCount=5" -Method POST -Headers $headers
$response | ConvertTo-Json

# Expected Response
{
  "articleId": 1,
  "totalClaps": 5,
  "userClaps": 5
}
```

### 6.2 Get Clap Info
```powershell
# PowerShell
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/engagement/articles/$articleId/clap" -Method GET
$response | ConvertTo-Json

# Expected Response
{
  "articleId": 1,
  "totalClaps": 5,
  "userClaps": 5
}
```

### 6.3 Bookmark Article
```powershell
# PowerShell
$headers = @{
    "Authorization" = "Bearer $token"
}

Invoke-RestMethod -Uri "http://localhost:8080/api/v1/engagement/articles/$articleId/bookmark" -Method POST -Headers $headers

# Expected Response: 200 OK (no content)
```

### 6.4 Create Bookmark Collection
```powershell
# PowerShell
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$body = @{
    name = "My Favorites"
    description = "A collection of my favorite articles"
    isPublic = $true
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/engagement/collections" -Method POST -Body $body -Headers $headers
$collectionId = $response.id
$response | ConvertTo-Json

# Expected Response
{
  "id": 1,
  "name": "My Favorites",
  "description": "A collection of my favorite articles",
  "isPublic": true,
  "articlesCount": 0,
  "createdAt": "2024-01-01T10:20:00"
}
```

---

## 7. Tag Management Endpoints

### 7.1 Get All Tags
```powershell
# PowerShell
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/tags" -Method GET
$response | ConvertTo-Json

# Expected Response
[
  "test",
  "updated",
  "sample"
]
```

### 7.2 Get Popular Tags
```powershell
# PowerShell
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/tags/popular?limit=5" -Method GET
$response | ConvertTo-Json

# Expected Response
[
  "test",
  "updated",
  "sample"
]
```

### 7.3 Get Tag Suggestions
```powershell
# PowerShell
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/tags/suggestions?query=te&limit=5" -Method GET
$response | ConvertTo-Json

# Expected Response
[
  {
    "name": "test",
    "count": 1
  }
]
```

---

## 8. Media Management Endpoints

### 8.1 Upload Media
```powershell
# PowerShell
$headers = @{
    "Authorization" = "Bearer $token"
}

# Create a test image file (base64 encoded)
$imageBytes = [System.Text.Encoding]::UTF8.GetBytes("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==")
$imageBase64 = [System.Convert]::ToBase64String($imageBytes)

$boundary = [System.Guid]::NewGuid().ToString()
$LF = "`r`n"

$bodyLines = @(
    "--$boundary",
    "Content-Disposition: form-data; name=`"file`"; filename=`"test.png`"",
    "Content-Type: image/png",
    "",
    [System.Text.Encoding]::UTF8.GetString($imageBytes),
    "--$boundary--"
) -join $LF

$headers["Content-Type"] = "multipart/form-data; boundary=$boundary"

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/media/upload" -Method POST -Body $bodyLines -Headers $headers
$response | ConvertTo-Json

# Expected Response
{
  "id": 1,
  "fileName": "test.png",
  "fileUrl": "https://example.com/uploads/test.png",
  "fileSize": 95,
  "contentType": "image/png",
  "uploadedAt": "2024-01-01T10:25:00"
}
```

---

## 9. Error Handling Tests

### 9.1 Invalid Authentication
```powershell
# PowerShell
try {
    $headers = @{
        "Authorization" = "Bearer invalid-token"
    }
    
    Invoke-RestMethod -Uri "http://localhost:8080/api/v1/users/profile" -Method GET -Headers $headers
} catch {
    $_.Exception.Response.StatusCode
    $_.Exception.Message
}

# Expected Response: 401 Unauthorized
```

### 9.2 Invalid Request Data
```powershell
# PowerShell
try {
    $body = @{
        username = ""
        email = "invalid-email"
        password = "123"
        confirmPassword = "456"
    } | ConvertTo-Json
    
    Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/register" -Method POST -Body $body -ContentType "application/json"
} catch {
    $_.Exception.Response.StatusCode
    $_.Exception.Message
}

# Expected Response: 400 Bad Request
```

### 9.3 Resource Not Found
```powershell
# PowerShell
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/v1/articles/slug/non-existent-article" -Method GET
} catch {
    $_.Exception.Response.StatusCode
    $_.Exception.Message
}

# Expected Response: 404 Not Found
```

---

## 10. Performance Tests

### 10.1 Load Test (Simple)
```powershell
# PowerShell
$startTime = Get-Date
$successCount = 0
$errorCount = 0

for ($i = 1; $i -le 100; $i++) {
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/articles/trending" -Method GET
        $successCount++
    } catch {
        $errorCount++
    }
}

$endTime = Get-Date
$duration = $endTime - $startTime

Write-Host "Load Test Results:"
Write-Host "Duration: $($duration.TotalSeconds) seconds"
Write-Host "Successful requests: $successCount"
Write-Host "Failed requests: $errorCount"
Write-Host "Average response time: $($duration.TotalSeconds / 100) seconds per request"
```

---

## 11. Complete Test Script

### 11.1 Full Test Suite
```powershell
# PowerShell - Complete Test Script
Write-Host "Starting API Test Suite..." -ForegroundColor Green

# 1. Health Check
Write-Host "1. Testing Health Check..." -ForegroundColor Yellow
try {
    $health = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method GET
    Write-Host "✓ Health Check: $($health.status)" -ForegroundColor Green
} catch {
    Write-Host "✗ Health Check Failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 2. Register User
Write-Host "2. Testing User Registration..." -ForegroundColor Yellow
try {
    $body = @{
        username = "testuser$(Get-Random)"
        email = "test$(Get-Random)@example.com"
        password = "password123"
        confirmPassword = "password123"
    } | ConvertTo-Json
    
    $authResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/register" -Method POST -Body $body -ContentType "application/json"
    $token = $authResponse.token
    Write-Host "✓ User Registration: Success" -ForegroundColor Green
} catch {
    Write-Host "✗ User Registration Failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 3. Create Article
Write-Host "3. Testing Article Creation..." -ForegroundColor Yellow
try {
    $headers = @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }
    
    $body = @{
        title = "Test Article $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')"
        content = "This is a test article content."
        summary = "Test summary"
        tags = @("test", "api")
        published = $true
    } | ConvertTo-Json
    
    $articleResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/articles" -Method POST -Body $body -Headers $headers
    $articleId = $articleResponse.id
    Write-Host "✓ Article Creation: Success (ID: $articleId)" -ForegroundColor Green
} catch {
    Write-Host "✗ Article Creation Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# 4. Test Article Retrieval
Write-Host "4. Testing Article Retrieval..." -ForegroundColor Yellow
try {
    $article = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/articles/slug/$($articleResponse.slug)" -Method GET
    Write-Host "✓ Article Retrieval: Success" -ForegroundColor Green
} catch {
    Write-Host "✗ Article Retrieval Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# 5. Test Comment Creation
Write-Host "5. Testing Comment Creation..." -ForegroundColor Yellow
try {
    $body = @{
        content = "This is a test comment."
    } | ConvertTo-Json
    
    $commentResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/comments/article/$articleId" -Method POST -Body $body -Headers $headers
    Write-Host "✓ Comment Creation: Success" -ForegroundColor Green
} catch {
    Write-Host "✗ Comment Creation Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# 6. Test Engagement
Write-Host "6. Testing Engagement..." -ForegroundColor Yellow
try {
    $clapResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/engagement/articles/$articleId/clap?clapCount=3" -Method POST -Headers $headers
    Write-Host "✓ Article Clap: Success" -ForegroundColor Green
} catch {
    Write-Host "✗ Article Clap Failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nTest Suite Completed!" -ForegroundColor Green
Write-Host "Token: $token" -ForegroundColor Cyan
Write-Host "Article ID: $articleId" -ForegroundColor Cyan
```

---

## 12. Troubleshooting

### 12.1 Common Issues

#### Application Not Starting
```powershell
# Check if port 8080 is available
netstat -an | findstr :8080

# Check application logs
Get-Content logs/application.log -Tail 50
```

#### Authentication Issues
```powershell
# Verify JWT token format
$token = "your-jwt-token"
$tokenParts = $token.Split('.')
if ($tokenParts.Length -ne 3) {
    Write-Host "Invalid JWT token format" -ForegroundColor Red
}
```

#### Database Connection Issues
```powershell
# Check H2 console
Start-Process "http://localhost:8080/h2-console"
```

### 12.2 Debug Mode
```powershell
# Enable debug logging
$env:LOGGING_LEVEL_ORG_EXAMPLE = "DEBUG"
mvn spring-boot:run
```

---

## 13. API Documentation Access

### 13.1 Swagger UI
```powershell
# Open Swagger UI in browser
Start-Process "http://localhost:8080/swagger-ui.html"
```

### 13.2 OpenAPI JSON
```powershell
# Get OpenAPI specification
Invoke-RestMethod -Uri "http://localhost:8080/api-docs" -Method GET | ConvertTo-Json -Depth 10
```

---

## 14. Monitoring and Metrics

### 14.1 Application Metrics
```powershell
# Get application metrics
Invoke-RestMethod -Uri "http://localhost:8080/actuator/metrics" -Method GET | ConvertTo-Json

# Get specific metric
Invoke-RestMethod -Uri "http://localhost:8080/actuator/metrics/http.server.requests" -Method GET | ConvertTo-Json
```

### 14.2 Health Details
```powershell
# Get detailed health information
Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method GET | ConvertTo-Json -Depth 5
```

---

## 15. Cleanup

### 15.1 Cleanup Test Data
```powershell
# Delete test article
$headers = @{
    "Authorization" = "Bearer $token"
}

Invoke-RestMethod -Uri "http://localhost:8080/api/v1/articles/$articleId" -Method DELETE -Headers $headers
Write-Host "Test data cleaned up" -ForegroundColor Green
```

This comprehensive testing guide covers all major endpoints and provides practical examples for testing the Medium Clone API. Use these scripts to verify functionality and troubleshoot issues. 