# Medium Clone API Documentation

## Overview
This document provides comprehensive documentation for all API endpoints in the Medium Clone application. The API follows RESTful principles and uses JWT authentication.

## Base URL
```
http://localhost:8080/api/v1
```

## Authentication
Most endpoints require JWT authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

---

## 1. Authentication Endpoints (`/api/v1/auth`)

### 1.1 Register User
- **POST** `/auth/register`
- **Description**: Register a new user account
- **Request Body**:
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "confirmPassword": "string"
}
```
- **Response**: `AuthResponseDto` with JWT token
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 1.2 Login User
- **POST** `/auth/login`
- **Description**: Authenticate user and get JWT token
- **Request Body**:
```json
{
  "username": "string",
  "password": "string"
}
```
- **Response**: `AuthResponseDto` with JWT token
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 1.3 Request Password Reset
- **POST** `/auth/forgot-password`
- **Description**: Request password reset email
- **Request Body**:
```json
{
  "email": "string"
}
```
- **Response**: `AuthResponseDto`
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 1.4 Reset Password
- **POST** `/auth/reset-password`
- **Description**: Reset password using token
- **Request Body**:
```json
{
  "token": "string",
  "newPassword": "string"
}
```
- **Response**: `AuthResponseDto`
- **Status Codes**: 200 (Success), 400 (Bad Request)

---

## 2. User Management Endpoints (`/api/v1/users`)

### 2.1 Get Current User Profile
- **GET** `/users/profile`
- **Description**: Get current authenticated user's profile
- **Authentication**: Required
- **Response**: `UserProfileDto`
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 2.2 Update User Profile
- **PUT** `/users/profile`
- **Description**: Update current user's profile
- **Authentication**: Required
- **Query Parameters**:
  - `bio` (optional): string
  - `profileImageUrl` (optional): string
- **Response**: `UserProfileDto`
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 2.3 Get User Profile by Username
- **GET** `/users/public/{username}`
- **Description**: Get public profile of a user
- **Path Parameters**:
  - `username`: string
- **Response**: `UserProfileDto`
- **Status Codes**: 200 (Success), 404 (Not Found)

### 2.4 Follow User
- **POST** `/users/follow/{username}`
- **Description**: Follow a user
- **Authentication**: Required
- **Path Parameters**:
  - `username`: string
- **Response**: String message
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 2.5 Unfollow User
- **DELETE** `/users/follow/{username}`
- **Description**: Unfollow a user
- **Authentication**: Required
- **Path Parameters**:
  - `username`: string
- **Response**: String message
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 2.6 Get User Followers
- **GET** `/users/{username}/followers`
- **Description**: Get list of user's followers
- **Path Parameters**:
  - `username`: string
- **Response**: List of `UserProfileDto`
- **Status Codes**: 200 (Success), 404 (Not Found)

### 2.7 Get User Following
- **GET** `/users/{username}/following`
- **Description**: Get list of users that the user follows
- **Path Parameters**:
  - `username`: string
- **Response**: List of `UserProfileDto`
- **Status Codes**: 200 (Success), 404 (Not Found)

---

## 3. Article Management Endpoints (`/api/v1/articles`)

### 3.1 Create Article
- **POST** `/articles`
- **Description**: Create a new article
- **Authentication**: Required
- **Request Body**: `ArticleCreateDto`
- **Response**: `ArticleResponseDto`
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 3.2 Edit Article
- **PUT** `/articles/{id}`
- **Description**: Edit an existing article
- **Authentication**: Required
- **Path Parameters**:
  - `id`: Long (article ID)
- **Request Body**: `ArticleEditDto`
- **Response**: `ArticleResponseDto`
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 3.3 Delete Article
- **DELETE** `/articles/{id}`
- **Description**: Delete an article
- **Authentication**: Required
- **Path Parameters**:
  - `id`: Long (article ID)
- **Response**: No content
- **Status Codes**: 204 (No Content), 400 (Bad Request)

### 3.4 Get Article by Slug
- **GET** `/articles/slug/{slug}`
- **Description**: Get article by its slug
- **Path Parameters**:
  - `slug`: string
- **Authentication**: Optional
- **Response**: `ArticleResponseDto`
- **Status Codes**: 200 (Success), 404 (Not Found)

### 3.5 Get Article Versions
- **GET** `/articles/{id}/versions`
- **Description**: Get version history of an article
- **Path Parameters**:
  - `id`: Long (article ID)
- **Response**: List of `ArticleVersionDto`
- **Status Codes**: 200 (Success)

### 3.6 Get User Feed
- **GET** `/articles/feed`
- **Description**: Get personalized feed for user
- **Query Parameters**:
  - `followedUserIds`: List<Long>
  - `page` (default: 0): int
  - `size` (default: 10): int
- **Response**: Page of `ArticleResponseDto`
- **Status Codes**: 200 (Success)

### 3.7 Get Trending Articles
- **GET** `/articles/trending`
- **Description**: Get trending articles
- **Query Parameters**:
  - `page` (default: 0): int
  - `size` (default: 10): int
- **Response**: Page of `ArticleResponseDto`
- **Status Codes**: 200 (Success)

### 3.8 Share Article
- **POST** `/articles/{articleId}/share`
- **Description**: Share an article
- **Authentication**: Required
- **Path Parameters**:
  - `articleId`: Long
- **Request Body**: `ShareDto`
- **Response**: No content
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 3.9 Bookmark Article
- **POST** `/articles/{articleId}/bookmark`
- **Description**: Bookmark an article
- **Authentication**: Required
- **Path Parameters**:
  - `articleId`: Long
- **Response**: No content
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 3.10 Remove Bookmark
- **DELETE** `/articles/{articleId}/bookmark`
- **Description**: Remove bookmark from article
- **Authentication**: Required
- **Path Parameters**:
  - `articleId`: Long
- **Response**: No content
- **Status Codes**: 204 (No Content), 400 (Bad Request)

### 3.11 Get Article Bookmarks
- **GET** `/articles/{articleId}/bookmarks`
- **Description**: Get bookmarks for an article
- **Path Parameters**:
  - `articleId`: Long
- **Query Parameters**:
  - `page` (default: 0): int
  - `size` (default: 10): int
- **Response**: Page of `BookmarkCollectionDto`
- **Status Codes**: 200 (Success)

### 3.12 Track Reading Time
- **POST** `/articles/{articleId}/reading-time`
- **Description**: Track time spent reading an article
- **Authentication**: Required
- **Path Parameters**:
  - `articleId`: Long
- **Query Parameters**:
  - `timeSpent`: Duration
- **Response**: No content
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 3.13 Get User Collections
- **GET** `/articles/collections`
- **Description**: Get user's article collections
- **Authentication**: Required
- **Response**: List of `ArticleCollectionDto`
- **Status Codes**: 200 (Success)

### 3.14 Create Collection
- **POST** `/articles/collections`
- **Description**: Create a new article collection
- **Authentication**: Required
- **Request Body**: `ArticleCollectionDto`
- **Response**: `ArticleCollectionDto`
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 3.15 Search Articles
- **GET** `/articles/search`
- **Description**: Search articles with filters
- **Query Parameters**:
  - `keyword` (optional): string
  - `tags` (optional): Set<string>
  - `author` (optional): string
  - `startDate` (optional): LocalDateTime
  - `endDate` (optional): LocalDateTime
  - `page` (default: 0): int
  - `size` (default: 10): int
- **Response**: Page of `ArticleResponseDto`
- **Status Codes**: 200 (Success)

### 3.16 Get Articles with Cursor Pagination
- **GET** `/articles/cursor`
- **Description**: Get articles using cursor-based pagination
- **Query Parameters**:
  - `cursor` (optional): string
  - `size` (default: 10): int
- **Response**: `CursorPage<ArticleResponseDto>`
- **Status Codes**: 200 (Success)

### 3.17 Get Article Recommendations
- **GET** `/articles/{articleId}/recommendations`
- **Description**: Get similar articles recommendations
- **Path Parameters**:
  - `articleId`: Long
- **Query Parameters**:
  - `limit` (default: 10): int
- **Authentication**: Optional
- **Response**: List of `ArticleResponseDto`
- **Status Codes**: 200 (Success)

### 3.18 Get Personalized Feed
- **GET** `/articles/feed/personalized`
- **Description**: Get personalized article feed
- **Authentication**: Required
- **Query Parameters**:
  - `limit` (default: 10): int
- **Response**: List of `ArticleResponseDto`
- **Status Codes**: 200 (Success)

---

## 4. Comment Management Endpoints (`/api/v1/comments`)

### 4.1 Create Comment
- **POST** `/comments/article/{articleId}`
- **Description**: Create a comment on an article
- **Authentication**: Required
- **Path Parameters**:
  - `articleId`: Long
- **Request Body**: `CommentCreateDto`
- **Response**: `CommentResponseDto`
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 4.2 Get Article Comments
- **GET** `/comments/article/{articleId}`
- **Description**: Get comments for an article
- **Path Parameters**:
  - `articleId`: Long
- **Query Parameters**:
  - `page` (default: 0): int
  - `size` (default: 10): int
- **Response**: List of `CommentResponseDto`
- **Status Codes**: 200 (Success)

### 4.3 Update Comment
- **PUT** `/comments/{commentId}`
- **Description**: Update a comment
- **Authentication**: Required
- **Path Parameters**:
  - `commentId`: Long
- **Request Body**: `CommentCreateDto`
- **Response**: `CommentResponseDto`
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 4.4 Delete Comment
- **DELETE** `/comments/{commentId}`
- **Description**: Delete a comment
- **Authentication**: Required
- **Path Parameters**:
  - `commentId`: Long
- **Response**: No content
- **Status Codes**: 204 (No Content), 400 (Bad Request)

### 4.5 Reply to Comment
- **POST** `/comments/{commentId}/reply`
- **Description**: Reply to a comment
- **Authentication**: Required
- **Path Parameters**:
  - `commentId`: Long
- **Request Body**: `CommentCreateDto`
- **Response**: `CommentResponseDto`
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 4.6 Get Comment Replies
- **GET** `/comments/{commentId}/replies`
- **Description**: Get replies to a comment
- **Path Parameters**:
  - `commentId`: Long
- **Query Parameters**:
  - `page` (default: 0): int
  - `size` (default: 10): int
- **Response**: List of `CommentResponseDto`
- **Status Codes**: 200 (Success)

---

## 5. Engagement Endpoints (`/api/v1/engagement`)

### 5.1 Clap Article
- **POST** `/engagement/articles/{articleId}/clap`
- **Description**: Clap for an article
- **Authentication**: Required
- **Path Parameters**:
  - `articleId`: Long
- **Query Parameters**:
  - `clapCount`: Integer
- **Response**: `ClapDto`
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 5.2 Get Clap Info
- **GET** `/engagement/articles/{articleId}/clap`
- **Description**: Get clap information for an article
- **Path Parameters**:
  - `articleId`: Long
- **Authentication**: Optional
- **Response**: `ClapDto`
- **Status Codes**: 200 (Success)

### 5.3 Create Comment (via Engagement)
- **POST** `/engagement/articles/{articleId}/comments`
- **Description**: Create a comment on an article
- **Authentication**: Required
- **Path Parameters**:
  - `articleId`: Long
- **Request Body**: `CommentCreateDto`
- **Response**: `CommentResponseDto`
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 5.4 Update Comment (via Engagement)
- **PUT** `/engagement/comments/{commentId}`
- **Description**: Update a comment
- **Authentication**: Required
- **Path Parameters**:
  - `commentId`: Long
- **Query Parameters**:
  - `content`: string
- **Response**: `CommentResponseDto`
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 5.5 Delete Comment (via Engagement)
- **DELETE** `/engagement/comments/{commentId}`
- **Description**: Delete a comment
- **Authentication**: Required
- **Path Parameters**:
  - `commentId`: Long
- **Response**: No content
- **Status Codes**: 204 (No Content), 400 (Bad Request)

### 5.6 Flag Comment
- **POST** `/engagement/comments/{commentId}/flag`
- **Description**: Flag a comment for moderation
- **Authentication**: Required
- **Path Parameters**:
  - `commentId`: Long
- **Response**: No content
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 5.7 Get Article Comments (via Engagement)
- **GET** `/engagement/articles/{articleId}/comments`
- **Description**: Get all comments for an article
- **Path Parameters**:
  - `articleId`: Long
- **Response**: List of `CommentResponseDto`
- **Status Codes**: 200 (Success)

### 5.8 Get Article Comments with Cursor Pagination
- **GET** `/engagement/articles/{articleId}/comments/cursor`
- **Description**: Get article comments using cursor pagination
- **Path Parameters**:
  - `articleId`: Long
- **Query Parameters**:
  - `cursor` (optional): string
  - `size` (default: 10): int
- **Response**: `CursorPage<CommentResponseDto>`
- **Status Codes**: 200 (Success)

### 5.9 Bookmark Article (via Engagement)
- **POST** `/engagement/articles/{articleId}/bookmark`
- **Description**: Bookmark an article
- **Authentication**: Required
- **Path Parameters**:
  - `articleId`: Long
- **Query Parameters**:
  - `collectionId` (optional): Long
- **Response**: No content
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 5.10 Remove Bookmark (via Engagement)
- **DELETE** `/engagement/articles/{articleId}/bookmark`
- **Description**: Remove bookmark from article
- **Authentication**: Required
- **Path Parameters**:
  - `articleId`: Long
- **Response**: No content
- **Status Codes**: 204 (No Content), 400 (Bad Request)

### 5.11 Create Bookmark Collection
- **POST** `/engagement/collections`
- **Description**: Create a bookmark collection
- **Authentication**: Required
- **Request Body**: `BookmarkCollectionDto`
- **Response**: `BookmarkCollectionDto`
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 5.12 Get User Collections (via Engagement)
- **GET** `/engagement/collections`
- **Description**: Get user's bookmark collections
- **Authentication**: Required
- **Response**: List of `BookmarkCollectionDto`
- **Status Codes**: 200 (Success)

### 5.13 Get Collection
- **GET** `/engagement/collections/{collectionId}`
- **Description**: Get a specific bookmark collection
- **Path Parameters**:
  - `collectionId`: Long
- **Authentication**: Optional
- **Response**: `BookmarkCollectionDto`
- **Status Codes**: 200 (Success), 404 (Not Found)

### 5.14 Get Share Info
- **GET** `/engagement/articles/{slug}/share`
- **Description**: Get sharing information for an article
- **Path Parameters**:
  - `slug`: string
- **Response**: `ShareDto`
- **Status Codes**: 200 (Success)

---

## 6. Tag Management Endpoints (`/api/v1/tags`)

### 6.1 Get All Tags
- **GET** `/tags`
- **Description**: Get all available tags
- **Response**: List of tags
- **Status Codes**: 200 (Success)

### 6.2 Get Popular Tags
- **GET** `/tags/popular`
- **Description**: Get most popular tags
- **Query Parameters**:
  - `limit` (default: 10): int
- **Response**: List of popular tags
- **Status Codes**: 200 (Success)

### 6.3 Get Tag Suggestions
- **GET** `/tags/suggestions`
- **Description**: Get tag suggestions based on input
- **Query Parameters**:
  - `query`: string
  - `limit` (default: 5): int
- **Response**: List of `TagSuggestionDto`
- **Status Codes**: 200 (Success)

---

## 7. Media Management Endpoints (`/api/v1/media`)

### 7.1 Upload Media
- **POST** `/media/upload`
- **Description**: Upload media files
- **Authentication**: Required
- **Request**: Multipart form data
- **Response**: `MediaResponseDto`
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 7.2 Get Media by ID
- **GET** `/media/{id}`
- **Description**: Get media information by ID
- **Path Parameters**:
  - `id`: Long
- **Response**: `MediaResponseDto`
- **Status Codes**: 200 (Success), 404 (Not Found)

### 7.3 Delete Media
- **DELETE** `/media/{id}`
- **Description**: Delete media file
- **Authentication**: Required
- **Path Parameters**:
  - `id`: Long
- **Response**: No content
- **Status Codes**: 204 (No Content), 400 (Bad Request)

### 7.4 Get User Media
- **GET** `/media/user`
- **Description**: Get current user's media files
- **Authentication**: Required
- **Query Parameters**:
  - `page` (default: 0): int
  - `size` (default: 10): int
- **Response**: Page of `MediaResponseDto`
- **Status Codes**: 200 (Success)

---

## 8. Content Organization Endpoints (`/api/v1/content-organization`)

### 8.1 Get User Collections
- **GET** `/content-organization/collections`
- **Description**: Get user's content collections
- **Authentication**: Required
- **Response**: List of collections
- **Status Codes**: 200 (Success)

### 8.2 Create Collection
- **POST** `/content-organization/collections`
- **Description**: Create a new content collection
- **Authentication**: Required
- **Request Body**: Collection data
- **Response**: Collection data
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 8.3 Update Collection
- **PUT** `/content-organization/collections/{id}`
- **Description**: Update a content collection
- **Authentication**: Required
- **Path Parameters**:
  - `id`: Long
- **Request Body**: Collection data
- **Response**: Collection data
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 8.4 Delete Collection
- **DELETE** `/content-organization/collections/{id}`
- **Description**: Delete a content collection
- **Authentication**: Required
- **Path Parameters**:
  - `id`: Long
- **Response**: No content
- **Status Codes**: 204 (No Content), 400 (Bad Request)

### 8.5 Add Article to Collection
- **POST** `/content-organization/collections/{collectionId}/articles/{articleId}`
- **Description**: Add article to collection
- **Authentication**: Required
- **Path Parameters**:
  - `collectionId`: Long
  - `articleId`: Long
- **Response**: No content
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 8.6 Remove Article from Collection
- **DELETE** `/content-organization/collections/{collectionId}/articles/{articleId}`
- **Description**: Remove article from collection
- **Authentication**: Required
- **Path Parameters**:
  - `collectionId`: Long
  - `articleId`: Long
- **Response**: No content
- **Status Codes**: 204 (No Content), 400 (Bad Request)

---

## 9. Analytics Endpoints (`/api/v1/analytics`)

### 9.1 Get Article Analytics
- **GET** `/analytics/articles/{articleId}`
- **Description**: Get analytics for an article
- **Authentication**: Required
- **Path Parameters**:
  - `articleId`: Long
- **Response**: `ArticleAnalyticsDto`
- **Status Codes**: 200 (Success), 404 (Not Found)

### 9.2 Get User Analytics
- **GET** `/analytics/user`
- **Description**: Get analytics for current user
- **Authentication**: Required
- **Response**: User analytics data
- **Status Codes**: 200 (Success)

---

## 10. Scheduled Tasks Endpoints (`/api/v1/scheduled-tasks`)

### 10.1 Get Scheduled Tasks
- **GET** `/scheduled-tasks`
- **Description**: Get all scheduled tasks
- **Authentication**: Required (Admin)
- **Response**: List of scheduled tasks
- **Status Codes**: 200 (Success)

### 10.2 Create Scheduled Task
- **POST** `/scheduled-tasks`
- **Description**: Create a new scheduled task
- **Authentication**: Required (Admin)
- **Request Body**: Task configuration
- **Response**: Task data
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 10.3 Update Scheduled Task
- **PUT** `/scheduled-tasks/{id}`
- **Description**: Update a scheduled task
- **Authentication**: Required (Admin)
- **Path Parameters**:
  - `id`: Long
- **Request Body**: Task configuration
- **Response**: Task data
- **Status Codes**: 200 (Success), 400 (Bad Request)

### 10.4 Delete Scheduled Task
- **DELETE** `/scheduled-tasks/{id}`
- **Description**: Delete a scheduled task
- **Authentication**: Required (Admin)
- **Path Parameters**:
  - `id`: Long
- **Response**: No content
- **Status Codes**: 204 (No Content), 400 (Bad Request)

### 10.5 Execute Task Now
- **POST** `/scheduled-tasks/{id}/execute`
- **Description**: Execute a scheduled task immediately
- **Authentication**: Required (Admin)
- **Path Parameters**:
  - `id`: Long
- **Response**: Task execution result
- **Status Codes**: 200 (Success), 400 (Bad Request)

---

## Data Transfer Objects (DTOs)

### AuthResponseDto
```json
{
  "token": "string",
  "message": "string",
  "success": boolean
}
```

### UserRegistrationDto
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "confirmPassword": "string"
}
```

### UserLoginDto
```json
{
  "username": "string",
  "password": "string"
}
```

### UserProfileDto
```json
{
  "id": "long",
  "username": "string",
  "email": "string",
  "bio": "string",
  "profileImageUrl": "string",
  "followersCount": "int",
  "followingCount": "int",
  "articlesCount": "int"
}
```

### ArticleCreateDto
```json
{
  "title": "string",
  "content": "string",
  "summary": "string",
  "tags": ["string"],
  "published": boolean
}
```

### ArticleResponseDto
```json
{
  "id": "long",
  "title": "string",
  "content": "string",
  "summary": "string",
  "slug": "string",
  "author": "UserProfileDto",
  "tags": ["string"],
  "published": boolean,
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime",
  "clapsCount": "int",
  "commentsCount": "int",
  "readingTime": "int"
}
```

### CommentCreateDto
```json
{
  "content": "string"
}
```

### CommentResponseDto
```json
{
  "id": "long",
  "content": "string",
  "author": "UserProfileDto",
  "articleId": "long",
  "parentCommentId": "long",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime"
}
```

### ClapDto
```json
{
  "articleId": "long",
  "totalClaps": "int",
  "userClaps": "int"
}
```

### BookmarkCollectionDto
```json
{
  "id": "long",
  "name": "string",
  "description": "string",
  "isPublic": boolean,
  "articlesCount": "int",
  "createdAt": "LocalDateTime"
}
```

### ShareDto
```json
{
  "url": "string",
  "title": "string",
  "description": "string"
}
```

---

## Error Responses

### Standard Error Format
```json
{
  "timestamp": "LocalDateTime",
  "status": "int",
  "error": "string",
  "message": "string",
  "path": "string"
}
```

### Common HTTP Status Codes
- **200**: OK - Request successful
- **201**: Created - Resource created successfully
- **204**: No Content - Request successful, no content to return
- **400**: Bad Request - Invalid request data
- **401**: Unauthorized - Authentication required
- **403**: Forbidden - Access denied
- **404**: Not Found - Resource not found
- **409**: Conflict - Resource conflict
- **500**: Internal Server Error - Server error

---

## Rate Limiting

The API implements rate limiting to prevent abuse:
- **General endpoints**: 100 requests per hour
- **Authentication endpoints**: 10 requests per hour
- **Rate limit headers**:
  - `X-RateLimit-Limit`: Maximum requests per window
  - `X-RateLimit-Remaining`: Remaining requests in current window
  - `X-RateLimit-Reset`: Time when the rate limit resets

---

## Pagination

Most list endpoints support pagination:
- **Page-based pagination**: Uses `page` and `size` parameters
- **Cursor-based pagination**: Uses `cursor` and `size` parameters
- **Response format**:
```json
{
  "content": ["array of items"],
  "totalElements": "long",
  "totalPages": "int",
  "size": "int",
  "number": "int",
  "first": boolean,
  "last": boolean
}
```

---

## Testing the API

### Using curl
```bash
# Register a user
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123","confirmPassword":"password123"}'

# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'

# Create article (with JWT token)
curl -X POST http://localhost:8080/api/v1/articles \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"title":"Test Article","content":"This is a test article","summary":"Test summary","tags":["test"],"published":true}'
```

### Using PowerShell
```powershell
# Register a user
$body = @{
    username = "testuser"
    email = "test@example.com"
    password = "password123"
    confirmPassword = "password123"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/register" -Method POST -Body $body -ContentType "application/json"

# First create the login body
$loginBody = @{
    username = "testuser"
    password = "password123"
} | ConvertTo-Json

# Then make the login request
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
```

---

## Notes

1. **Authentication**: Most endpoints require JWT authentication. Include the token in the Authorization header.
2. **Validation**: Request bodies are validated using Bean Validation annotations.
3. **CORS**: The API supports CORS for cross-origin requests.
4. **File Uploads**: Media uploads support common image formats (JPG, PNG, GIF, WebP).
5. **Search**: Article search supports keyword, tag, author, and date range filtering.
6. **Recommendations**: Article recommendations are based on content similarity and user preferences.
7. **Analytics**: Analytics data is collected for articles and user engagement.
8. **Scheduling**: The system supports scheduled tasks for maintenance and notifications.