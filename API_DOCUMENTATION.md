# Medium Clone API Documentation

## Overview

This document provides comprehensive documentation for the Medium Clone backend API. The API follows RESTful principles and uses JWT authentication for secure access.

**Base URL**: `http://localhost:8080`  
**API Version**: v1  
**Content Type**: `application/json`

## Authentication

The API uses JWT (JSON Web Token) authentication. Include the JWT token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

## API Endpoints

### 1. Authentication (`/api/v1/auth`)

#### Register User
- **POST** `/api/v1/auth/register`
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
- **Response**: `201 Created`
  ```json
  {
    "token": "jwt-token",
    "user": {
      "id": 1,
      "username": "string",
      "email": "string",
      "emailVerified": false
    }
  }
  ```

#### Login User
- **POST** `/api/v1/auth/login`
- **Description**: Authenticate user and get JWT token
- **Request Body**:
  ```json
  {
    "usernameOrEmail": "string",
    "password": "string"
  }
  ```
- **Response**: `200 OK`
  ```json
  {
    "token": "jwt-token",
    "user": {
      "id": 1,
      "username": "string",
      "email": "string"
    }
  }
  ```

#### Request Password Reset
- **POST** `/api/v1/auth/forgot-password`
- **Description**: Send password reset email
- **Request Body**:
  ```json
  {
    "email": "string"
  }
  ```
- **Response**: `200 OK`
  ```json
  {
    "message": "Password reset email sent"
  }
  ```

#### Reset Password
- **POST** `/api/v1/auth/reset-password`
- **Description**: Reset password using token
- **Request Body**:
  ```json
  {
    "token": "string",
    "newPassword": "string"
  }
  ```
- **Response**: `200 OK`
  ```json
  {
    "message": "Password reset successfully"
  }
  ```

### 2. User Management (`/api/v1/users`)

#### Get User Profile
- **GET** `/api/v1/users/profile`
- **Description**: Get current user's profile
- **Authentication**: Required
- **Response**: `200 OK`
  ```json
  {
    "id": 1,
    "username": "string",
    "email": "string",
    "bio": "string",
    "profileImageUrl": "string",
    "followersCount": 0,
    "followingCount": 0,
    "createdAt": "2024-01-01T00:00:00"
  }
  ```

#### Update User Profile
- **PUT** `/api/v1/users/profile`
- **Description**: Update current user's profile
- **Authentication**: Required
- **Request Body**:
  ```json
  {
    "username": "string",
    "bio": "string",
    "profileImageUrl": "string"
  }
  ```
- **Response**: `200 OK`

#### Get User by Username
- **GET** `/api/v1/users/{username}`
- **Description**: Get public profile of a user
- **Response**: `200 OK`
  ```json
  {
    "id": 1,
    "username": "string",
    "bio": "string",
    "profileImageUrl": "string",
    "followersCount": 0,
    "followingCount": 0,
    "createdAt": "2024-01-01T00:00:00"
  }
  ```

#### Follow User
- **POST** `/api/v1/users/{userId}/follow`
- **Description**: Follow a user
- **Authentication**: Required
- **Response**: `200 OK`
  ```json
  {
    "message": "User followed successfully"
  }
  ```

#### Unfollow User
- **DELETE** `/api/v1/users/{userId}/follow`
- **Description**: Unfollow a user
- **Authentication**: Required
- **Response**: `200 OK`
  ```json
  {
    "message": "User unfollowed successfully"
  }
  ```

#### Search Users
- **GET** `/api/v1/users/search?q={query}`
- **Description**: Search users by username or bio
- **Response**: `200 OK`
  ```json
  [
    {
      "id": 1,
      "username": "string",
      "bio": "string",
      "profileImageUrl": "string"
    }
  ]
  ```

### 3. Articles (`/api/v1/articles`)

#### Get Articles (Paginated)
- **GET** `/api/v1/articles?page={page}&size={size}&sort={sort}`
- **Description**: Get paginated list of published articles
- **Query Parameters**:
  - `page`: Page number (default: 0)
  - `size`: Page size (default: 20, max: 100)
  - `sort`: Sort field (default: "publishedAt,desc")
- **Response**: `200 OK`
  ```json
  {
    "content": [
      {
        "id": 1,
        "title": "string",
        "slug": "string",
        "summary": "string",
        "coverImageUrl": "string",
        "readTimeMinutes": 5,
        "viewCount": 100,
        "clapsCount": 50,
        "commentCount": 10,
        "author": {
          "id": 1,
          "username": "string",
          "profileImageUrl": "string"
        },
        "tags": ["tag1", "tag2"],
        "publishedAt": "2024-01-01T00:00:00"
      }
    ],
    "totalElements": 100,
    "totalPages": 5,
    "currentPage": 0
  }
  ```

#### Get Article by Slug
- **GET** `/api/v1/articles/{slug}`
- **Description**: Get a specific article by its slug
- **Response**: `200 OK`
  ```json
  {
    "id": 1,
    "title": "string",
    "slug": "string",
    "content": "string",
    "summary": "string",
    "coverImageUrl": "string",
    "readTimeMinutes": 5,
    "viewCount": 100,
    "clapsCount": 50,
    "commentCount": 10,
    "author": {
      "id": 1,
      "username": "string",
      "profileImageUrl": "string"
    },
    "tags": ["tag1", "tag2"],
    "publishedAt": "2024-01-01T00:00:00"
  }
  ```

#### Create Article
- **POST** `/api/v1/articles`
- **Description**: Create a new article
- **Authentication**: Required
- **Request Body**:
  ```json
  {
    "title": "string",
    "content": "string",
    "summary": "string",
    "coverImageUrl": "string",
    "tags": ["tag1", "tag2"],
    "published": true
  }
  ```
- **Response**: `201 Created`

#### Update Article
- **PUT** `/api/v1/articles/{id}`
- **Description**: Update an existing article
- **Authentication**: Required (author only)
- **Request Body**: Same as create
- **Response**: `200 OK`

#### Delete Article
- **DELETE** `/api/v1/articles/{id}`
- **Description**: Delete an article
- **Authentication**: Required (author only)
- **Response**: `204 No Content`

#### Search Articles
- **GET** `/api/v1/articles/search?q={query}&page={page}&size={size}`
- **Description**: Search articles by title or content
- **Response**: `200 OK`

#### Get Trending Articles
- **GET** `/api/v1/articles/trending?page={page}&size={size}`
- **Description**: Get trending articles based on views, claps, and comments
- **Response**: `200 OK`

#### Get Articles by Tag
- **GET** `/api/v1/articles/tag/{tagName}?page={page}&size={size}`
- **Description**: Get articles by specific tag
- **Response**: `200 OK`

#### Get Articles by Author
- **GET** `/api/v1/articles/author/{username}?page={page}&size={size}`
- **Description**: Get articles by specific author
- **Response**: `200 OK`

### 4. Engagement (`/api/v1/engagement`)

#### Clap Article
- **POST** `/api/v1/engagement/articles/{articleId}/clap`
- **Description**: Add clap to an article
- **Authentication**: Required
- **Request Body**:
  ```json
  {
    "clapCount": 5
  }
  ```
- **Response**: `200 OK`

#### Remove Clap
- **DELETE** `/api/v1/engagement/articles/{articleId}/clap`
- **Description**: Remove clap from an article
- **Authentication**: Required
- **Response**: `200 OK`

#### Create Comment
- **POST** `/api/v1/engagement/articles/{articleId}/comments`
- **Description**: Add comment to an article
- **Authentication**: Required
- **Request Body**:
  ```json
  {
    "content": "string",
    "parentId": 1
  }
  ```
- **Response**: `201 Created`

#### Update Comment
- **PUT** `/api/v1/engagement/comments/{commentId}`
- **Description**: Update a comment
- **Authentication**: Required (author only)
- **Request Body**:
  ```json
  {
    "content": "string"
  }
  ```
- **Response**: `200 OK`

#### Delete Comment
- **DELETE** `/api/v1/engagement/comments/{commentId}`
- **Description**: Delete a comment
- **Authentication**: Required (author only)
- **Response**: `204 No Content`

#### Get Comments
- **GET** `/api/v1/engagement/articles/{articleId}/comments?page={page}&size={size}`
- **Description**: Get comments for an article
- **Response**: `200 OK`

#### Bookmark Article
- **POST** `/api/v1/engagement/articles/{articleId}/bookmark`
- **Description**: Bookmark an article
- **Authentication**: Required
- **Request Body**:
  ```json
  {
    "collectionId": 1
  }
  ```
- **Response**: `200 OK`

#### Remove Bookmark
- **DELETE** `/api/v1/engagement/articles/{articleId}/bookmark`
- **Description**: Remove bookmark from an article
- **Authentication**: Required
- **Response**: `200 OK`

#### Get User Bookmarks
- **GET** `/api/v1/engagement/bookmarks?page={page}&size={size}`
- **Description**: Get current user's bookmarks
- **Authentication**: Required
- **Response**: `200 OK`

#### Share Article
- **POST** `/api/v1/engagement/articles/{articleId}/share`
- **Description**: Share an article
- **Authentication**: Required
- **Request Body**:
  ```json
  {
    "platform": "twitter|facebook|linkedin"
  }
  ```
- **Response**: `200 OK`

### 5. Content Organization (`/api/v1/content`)

#### Get Tags
- **GET** `/api/v1/content/tags?page={page}&size={size}`
- **Description**: Get all tags
- **Response**: `200 OK`
  ```json
  [
    {
      "id": 1,
      "name": "string",
      "description": "string",
      "articleCount": 10,
      "trendingScore": 0.8
    }
  ]
  ```

#### Get Trending Tags
- **GET** `/api/v1/content/tags/trending`
- **Description**: Get trending tags
- **Response**: `200 OK`

#### Search Tags
- **GET** `/api/v1/content/tags/search?q={query}`
- **Description**: Search tags by name
- **Response**: `200 OK`

#### Create Tag
- **POST** `/api/v1/content/tags`
- **Description**: Create a new tag
- **Authentication**: Required (admin only)
- **Request Body**:
  ```json
  {
    "name": "string",
    "description": "string"
  }
  ```
- **Response**: `201 Created`

#### Create Article Collection
- **POST** `/api/v1/content/collections`
- **Description**: Create a new article collection
- **Authentication**: Required
- **Request Body**:
  ```json
  {
    "name": "string",
    "description": "string",
    "isPublic": true
  }
  ```
- **Response**: `201 Created`

#### Get Collections
- **GET** `/api/v1/content/collections?page={page}&size={size}`
- **Description**: Get article collections
- **Response**: `200 OK`

#### Get Collection
- **GET** `/api/v1/content/collections/{id}`
- **Description**: Get a specific collection
- **Response**: `200 OK`

#### Update Collection
- **PUT** `/api/v1/content/collections/{id}`
- **Description**: Update a collection
- **Authentication**: Required (owner only)
- **Request Body**: Same as create
- **Response**: `200 OK`

#### Delete Collection
- **DELETE** `/api/v1/content/collections/{id}`
- **Description**: Delete a collection
- **Authentication**: Required (owner only)
- **Response**: `204 No Content`

#### Add Article to Collection
- **POST** `/api/v1/content/collections/{id}/articles/{articleId}`
- **Description**: Add article to collection
- **Authentication**: Required (owner only)
- **Response**: `200 OK`

#### Remove Article from Collection
- **DELETE** `/api/v1/content/collections/{id}/articles/{articleId}`
- **Description**: Remove article from collection
- **Authentication**: Required (owner only)
- **Response**: `200 OK`

#### Get Drafts
- **GET** `/api/v1/content/drafts?page={page}&size={size}`
- **Description**: Get user's drafts
- **Authentication**: Required
- **Response**: `200 OK`

#### Get Draft
- **GET** `/api/v1/content/drafts/{id}`
- **Description**: Get a specific draft
- **Authentication**: Required (author only)
- **Response**: `200 OK`

#### Create Draft
- **POST** `/api/v1/content/drafts`
- **Description**: Create a new draft
- **Authentication**: Required
- **Request Body**:
  ```json
  {
    "title": "string",
    "content": "string",
    "summary": "string",
    "coverImageUrl": "string",
    "tags": ["tag1", "tag2"]
  }
  ```
- **Response**: `201 Created`

#### Update Draft
- **PUT** `/api/v1/content/drafts/{id}`
- **Description**: Update a draft
- **Authentication**: Required (author only)
- **Request Body**: Same as create
- **Response**: `200 OK`

#### Delete Draft
- **DELETE** `/api/v1/content/drafts/{id}`
- **Description**: Delete a draft
- **Authentication**: Required (author only)
- **Response**: `204 No Content`

#### Publish Draft
- **POST** `/api/v1/content/drafts/{id}/publish`
- **Description**: Publish a draft as an article
- **Authentication**: Required (author only)
- **Response**: `200 OK`

### 6. Media Management (`/api/v1/media`)

#### Upload Media
- **POST** `/api/v1/media/upload`
- **Description**: Upload media file
- **Authentication**: Required
- **Content-Type**: `multipart/form-data`
- **Request Body**:
  - `file`: Media file
  - `type`: "image" | "video" | "document"
- **Response**: `201 Created`
  ```json
  {
    "id": 1,
    "originalFilename": "string",
    "storedFilename": "string",
    "contentType": "string",
    "fileSize": 1024,
    "url": "string",
    "cdnUrl": "string",
    "uploadedAt": "2024-01-01T00:00:00"
  }
  ```

#### Get User Media
- **GET** `/api/v1/media?page={page}&size={size}`
- **Description**: Get current user's uploaded media
- **Authentication**: Required
- **Response**: `200 OK`

#### Delete Media
- **DELETE** `/api/v1/media/{id}`
- **Description**: Delete media file
- **Authentication**: Required (owner only)
- **Response**: `204 No Content`

#### Get Media Info
- **GET** `/api/v1/media/{id}`
- **Description**: Get media file information
- **Response**: `200 OK`

### 7. Analytics (`/api/v1/analytics`)

#### Get User Analytics
- **GET** `/api/v1/analytics/user`
- **Description**: Get current user's analytics
- **Authentication**: Required
- **Response**: `200 OK`
  ```json
  {
    "totalArticles": 10,
    "totalViews": 1000,
    "totalClaps": 500,
    "totalComments": 100,
    "readingTime": 120,
    "popularArticles": [
      {
        "id": 1,
        "title": "string",
        "viewCount": 100,
        "clapsCount": 50
      }
    ]
  }
  ```

#### Get Article Analytics
- **GET** `/api/v1/analytics/articles/{articleId}`
- **Description**: Get analytics for a specific article
- **Authentication**: Required (author only)
- **Response**: `200 OK`
  ```json
  {
    "viewCount": 100,
    "clapsCount": 50,
    "commentCount": 10,
    "bookmarkCount": 5,
    "shareCount": 3,
    "readingTime": 5,
    "engagementRate": 0.8
  }
  ```

#### Get Reading History
- **GET** `/api/v1/analytics/reading-history?page={page}&size={size}`
- **Description**: Get user's reading history
- **Authentication**: Required
- **Response**: `200 OK`

### 8. Recommendations (`/api/v1/recommendations`)

#### Get Personalized Recommendations
- **GET** `/api/v1/recommendations/personalized?limit={limit}`
- **Description**: Get personalized article recommendations
- **Authentication**: Required
- **Response**: `200 OK`

#### Get "More Like This" Recommendations
- **GET** `/api/v1/recommendations/similar/{articleId}?limit={limit}`
- **Description**: Get articles similar to the given article
- **Response**: `200 OK`

#### Get Trending Recommendations
- **GET** `/api/v1/recommendations/trending?limit={limit}`
- **Description**: Get trending article recommendations
- **Response**: `200 OK`

### 9. Admin Tasks (`/api/v1/admin/scheduled-tasks`)

#### Trigger Daily Email Digest
- **POST** `/api/v1/admin/scheduled-tasks/email-digest/daily`
- **Description**: Manually trigger daily email digest
- **Authentication**: Required (admin only)
- **Response**: `200 OK`

#### Trigger Weekly Email Digest
- **POST** `/api/v1/admin/scheduled-tasks/email-digest/weekly`
- **Description**: Manually trigger weekly email digest
- **Authentication**: Required (admin only)
- **Response**: `200 OK`

#### Trigger Draft Cleanup
- **POST** `/api/v1/admin/scheduled-tasks/cleanup/drafts`
- **Description**: Manually trigger draft cleanup
- **Authentication**: Required (admin only)
- **Response**: `200 OK`

#### Trigger Notification Cleanup
- **POST** `/api/v1/admin/scheduled-tasks/cleanup/notifications`
- **Description**: Manually trigger notification cleanup
- **Authentication**: Required (admin only)
- **Response**: `200 OK`

#### Trigger Media Cleanup
- **POST** `/api/v1/admin/scheduled-tasks/cleanup/media`
- **Description**: Manually trigger media cleanup
- **Authentication**: Required (admin only)
- **Response**: `200 OK`

#### Trigger Spam Cleanup
- **POST** `/api/v1/admin/scheduled-tasks/cleanup/spam`
- **Description**: Manually trigger spam cleanup
- **Authentication**: Required (admin only)
- **Response**: `200 OK`

#### Trigger Database Backup
- **POST** `/api/v1/admin/scheduled-tasks/backup/database`
- **Description**: Manually trigger database backup
- **Authentication**: Required (admin only)
- **Response**: `200 OK`

#### Trigger Full Backup
- **POST** `/api/v1/admin/scheduled-tasks/backup/full`
- **Description**: Manually trigger full backup
- **Authentication**: Required (admin only)
- **Response**: `200 OK`

#### Trigger File Backup
- **POST** `/api/v1/admin/scheduled-tasks/backup/files`
- **Description**: Manually trigger file backup
- **Authentication**: Required (admin only)
- **Response**: `200 OK`

#### Get Task Status
- **GET** `/api/v1/admin/scheduled-tasks/status`
- **Description**: Get scheduled task status and statistics
- **Authentication**: Required (admin only)
- **Response**: `200 OK`

## Error Responses

### Standard Error Format
```json
{
  "error": "Error message",
  "timestamp": "2024-01-01T00:00:00",
  "path": "/api/v1/endpoint",
  "status": 400
}
```

### Common HTTP Status Codes

- **200 OK**: Request successful
- **201 Created**: Resource created successfully
- **204 No Content**: Request successful, no content to return
- **400 Bad Request**: Invalid request data
- **401 Unauthorized**: Authentication required
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource not found
- **409 Conflict**: Resource conflict (e.g., duplicate username)
- **422 Unprocessable Entity**: Validation errors
- **500 Internal Server Error**: Server error

### Validation Error Format
```json
{
  "fieldName": "Error message",
  "anotherField": "Another error message"
}
```

## Rate Limiting

The API implements rate limiting to prevent abuse:

- **Default**: 100 requests per hour per IP
- **Authentication endpoints**: 10 requests per hour per IP
- **File uploads**: 50 requests per hour per user

Rate limit headers are included in responses:
```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1640995200
```

## Pagination

Most list endpoints support pagination with the following query parameters:

- `page`: Page number (0-based, default: 0)
- `size`: Page size (default: 20, max: 100)
- `sort`: Sort field and direction (e.g., "createdAt,desc")

Response includes pagination metadata:
```json
{
  "content": [...],
  "totalElements": 100,
  "totalPages": 5,
  "currentPage": 0,
  "size": 20,
  "first": true,
  "last": false
}
```

## Cursor-Based Pagination

Some endpoints use cursor-based pagination for better performance:

- `cursor`: Base64 encoded cursor for next page
- `limit`: Number of items to return (default: 20, max: 100)

Response format:
```json
{
  "items": [...],
  "nextCursor": "base64-encoded-cursor",
  "hasMore": true
}
```

## File Upload

### Supported File Types
- **Images**: JPG, JPEG, PNG, GIF, WebP
- **Videos**: MP4, AVI, MOV
- **Documents**: PDF, DOC, DOCX

### File Size Limits
- **Images**: 10MB
- **Videos**: 100MB
- **Documents**: 50MB

### Upload Process
1. Upload file to `/api/v1/media/upload`
2. Receive media ID and URLs
3. Use media ID in article creation/update

## WebSocket Endpoints

### Real-time Notifications
- **Endpoint**: `/ws/notifications`
- **Description**: WebSocket connection for real-time notifications
- **Authentication**: Required (JWT token in query parameter)

### Message Format
```json
{
  "type": "notification",
  "data": {
    "id": 1,
    "title": "string",
    "message": "string",
    "type": "comment|clap|follow",
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

## API Versioning

The API uses URL versioning:
- Current version: `/api/v1/`
- Future versions: `/api/v2/`, `/api/v3/`, etc.

## SDKs and Libraries

### JavaScript/TypeScript
```javascript
// Example using fetch
const response = await fetch('/api/v1/articles', {
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
});
```

### Python
```python
import requests

headers = {'Authorization': f'Bearer {token}'}
response = requests.get('http://localhost:8080/api/v1/articles', headers=headers)
```

### cURL Examples

#### Register User
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "confirmPassword": "password123"
  }'
```

#### Create Article
```bash
curl -X POST http://localhost:8080/api/v1/articles \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My First Article",
    "content": "Article content here...",
    "summary": "Article summary",
    "tags": ["technology", "programming"],
    "published": true
  }'
```

## Testing

### Swagger UI
Access the interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

### Postman Collection
A Postman collection is available for testing all endpoints.

## Support

For API support and questions:
- **Email**: api-support@yourdomain.com
- **Documentation**: https://docs.yourdomain.com/api
- **Status Page**: https://status.yourdomain.com 