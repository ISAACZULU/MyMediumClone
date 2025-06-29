# Production Setup Guide

## CDN Configuration

### 1. AWS CloudFront Setup

1. **Create S3 Bucket for Media Storage**
   ```bash
   aws s3 mb s3://your-medium-clone-media
   aws s3api put-bucket-cors --bucket your-medium-clone-media --cors-configuration file://cors.json
   ```

2. **Create CloudFront Distribution**
   - Origin: S3 bucket
   - Behaviors: Cache based on file extensions
   - Functions: Image optimization

3. **Update application.properties**
   ```properties
   app.cdn.enabled=true
   app.cdn.provider=cloudfront
   app.cdn.base-url=https://d1234.cloudfront.net
   app.cdn.region=us-east-1
   app.cdn.bucket=your-medium-clone-media
   app.cdn.access-key=your-access-key
   app.cdn.secret-key=your-secret-key
   ```

### 2. Cloudinary Setup

1. **Sign up for Cloudinary account**
2. **Update application.properties**
   ```properties
   app.cdn.enabled=true
   app.cdn.provider=cloudinary
   app.cdn.base-url=https://res.cloudinary.com/your-cloud-name
   ```

### 3. Imgix Setup

1. **Create Imgix source**
2. **Update application.properties**
   ```properties
   app.cdn.enabled=true
   app.cdn.provider=imgix
   app.cdn.base-url=https://your-subdomain.imgix.net
   ```

## Recommendation System Tuning

### 1. Algorithm Weights Configuration

The recommendation system uses configurable weights for different factors:

```java
// In AdvancedRecommendationService.java
private static final double CONTENT_SIMILARITY_WEIGHT = 0.3;
private static final double USER_BEHAVIOR_WEIGHT = 0.4;
private static final double POPULARITY_WEIGHT = 0.2;
private static final double RECENCY_WEIGHT = 0.1;
```

### 2. User Feedback Integration

Track user interactions to improve recommendations:

```java
// Record feedback when users interact with articles
userFeedbackService.recordFeedback(user, article, FeedbackType.CLICK, "recommendation");
userFeedbackService.recordFeedback(user, article, FeedbackType.READ, "feed");
userFeedbackService.recordFeedback(user, article, FeedbackType.CLAP, "article");
```

### 3. A/B Testing Setup

Implement A/B testing for recommendation algorithms:

```properties
# application.properties
app.recommendation.ab-testing.enabled=true
app.recommendation.ab-testing.algorithm-a-weight=0.5
app.recommendation.ab-testing.algorithm-b-weight=0.5
```

### 4. Performance Optimization

1. **Caching Recommendations**
   ```java
   @Cacheable(value = "recommendations", key = "#username")
   public List<ArticleResponseDto> getPersonalizedRecommendations(String username, int limit)
   ```

2. **Background Processing**
   - Use Spring's @Async for recommendation calculations
   - Implement scheduled tasks for user behavior analysis

3. **Database Indexing**
   ```sql
   -- Add indexes for recommendation queries
   CREATE INDEX idx_reading_history_user_read_at ON reading_history(user_id, read_at);
   CREATE INDEX idx_clap_article_user ON clap(article_id, user_id);
   CREATE INDEX idx_article_tags ON article_tags(article_id, tag_id);
   ```

## Monitoring and Analytics

### 1. Recommendation Performance Metrics

Track these metrics to optimize recommendations:

- **Click-through Rate (CTR)**: Percentage of recommended articles clicked
- **Engagement Rate**: Percentage of articles that received claps/bookmarks
- **Diversity Score**: Variety of content types in recommendations
- **Freshness Score**: Balance of new vs. popular content

### 2. CDN Performance Monitoring

Monitor CDN performance with these metrics:

- **Cache Hit Rate**: Should be > 90%
- **Response Time**: Should be < 100ms
- **Error Rate**: Should be < 0.1%
- **Bandwidth Usage**: Monitor for cost optimization

### 3. Application Performance Monitoring

Use tools like:
- **Spring Boot Actuator** for application metrics
- **Micrometer** for custom metrics
- **Prometheus + Grafana** for visualization

## Security Considerations

### 1. CDN Security

1. **Signed URLs** for private content
2. **Geographic restrictions** if needed
3. **Rate limiting** on CDN level
4. **HTTPS enforcement**

### 2. Recommendation Security

1. **Input validation** for user feedback
2. **Rate limiting** on feedback endpoints
3. **Data privacy** compliance (GDPR, CCPA)
4. **Audit logging** for recommendation decisions

## Deployment Checklist

### Pre-deployment
- [ ] CDN configured and tested
- [ ] Recommendation algorithms tuned
- [ ] Performance benchmarks established
- [ ] Security measures implemented
- [ ] Monitoring tools configured

### Post-deployment
- [ ] Monitor recommendation quality
- [ ] Track CDN performance
- [ ] Gather user feedback
- [ ] Iterate on algorithm weights
- [ ] Scale infrastructure as needed

## Troubleshooting

### Common CDN Issues
1. **Images not loading**: Check CORS configuration
2. **Slow response times**: Verify cache settings
3. **High costs**: Optimize image sizes and formats

### Common Recommendation Issues
1. **Poor recommendations**: Adjust algorithm weights
2. **Cold start problem**: Implement fallback strategies
3. **Performance issues**: Add caching and optimize queries 