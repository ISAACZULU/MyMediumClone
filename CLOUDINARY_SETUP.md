# Cloudinary Setup for Medium Clone

## Step 1: Create Cloudinary Account

1. **Sign up at [Cloudinary.com](https://cloudinary.com)**
   - Choose the free plan (25GB storage, 25GB bandwidth/month)
   - Or select a paid plan for more features

2. **Get Your Credentials**
   - Go to Dashboard → Account Details
   - Note down:
     - Cloud Name
     - API Key
     - API Secret

## Step 2: Configure Application

### Update `application.properties`

Replace the placeholder values with your actual Cloudinary credentials:

```properties
# Cloudinary Configuration
app.cloudinary.cloud-name=your-actual-cloud-name
app.cloudinary.api-key=your-actual-api-key
app.cloudinary.api-secret=your-actual-api-secret
app.cloudinary.folder=medium-clone
app.cloudinary.auto-optimize=true

# Enable Cloudinary as CDN provider
app.cdn.enabled=true
app.cdn.provider=cloudinary
app.cdn.base-url=https://res.cloudinary.com/your-actual-cloud-name
```

### Example Configuration:
```properties
app.cloudinary.cloud-name=my-medium-clone
app.cloudinary.api-key=123456789012345
app.cloudinary.api-secret=abcdefghijklmnopqrstuvwxyz123456
app.cloudinary.folder=medium-clone
app.cloudinary.auto-optimize=true

app.cdn.enabled=true
app.cdn.provider=cloudinary
app.cdn.base-url=https://res.cloudinary.com/my-medium-clone
```

## Step 3: Test the Setup

### 1. Start Your Application
```bash
mvn spring-boot:run
```

### 2. Test Image Upload
Use the new Cloudinary upload endpoint:

```bash
curl -X POST \
  http://localhost:8080/api/v1/media/cloudinary/upload \
  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
  -F 'file=@/path/to/your/image.jpg'
```

### 3. Test Image Optimization
```bash
curl "http://localhost:8080/api/v1/media/cloudinary/optimize?publicId=medium-clone/your-image-id&width=800&height=600&format=webp"
```

## Step 4: Cloudinary Features Available

### Automatic Image Optimization
- **Format Optimization**: Automatically converts to WebP/AVIF for supported browsers
- **Quality Optimization**: Reduces file size while maintaining quality
- **Responsive Images**: Automatically serves appropriate sizes

### Image Transformations
- **Resize**: `width`, `height`, `crop` parameters
- **Quality**: `quality=auto` for optimal compression
- **Format**: `format=auto` for best format selection

### Artistic Filters
Available filters include:
- `art:audrey` - Vintage look
- `art:aurora` - Soft, dreamy effect
- `art:ash` - Black and white
- `art:athena` - Dramatic lighting
- `art:peacock` - Vibrant colors

### Responsive Images
- Automatically serves appropriate image sizes based on device
- Reduces bandwidth usage
- Improves page load times

## Step 5: Advanced Configuration

### Custom Transformations
You can add custom transformations in `CloudinaryService.java`:

```java
public String generateCustomTransformation(String publicId) {
    Map<String, Object> transformation = ObjectUtils.asMap(
        "width", 800,
        "height", 600,
        "crop", "fill",
        "gravity", "face",
        "quality", "auto",
        "format", "auto",
        "effect", "art:audrey"
    );
    
    return cloudinary.url()
            .transformation(transformation)
            .generate(publicId);
}
```

### Folder Organization
Images are organized in Cloudinary folders:
- `medium-clone/` - Main folder
- `medium-clone/users/{userId}/` - User-specific images
- `medium-clone/articles/{articleId}/` - Article images

### Security Settings
1. **Upload Presets**: Create upload presets in Cloudinary dashboard
2. **Allowed Formats**: Restrict to image formats only
3. **File Size Limits**: Set maximum file size limits

## Step 6: Monitoring and Analytics

### Cloudinary Dashboard
- **Usage Statistics**: Monitor bandwidth and storage usage
- **Performance Metrics**: Track image delivery performance
- **Error Logs**: View upload and transformation errors

### Application Logs
Monitor these logs for Cloudinary operations:
```bash
tail -f logs/application.log | grep Cloudinary
```

## Step 7: Production Considerations

### 1. Environment Variables
Store credentials securely:
```bash
export CLOUDINARY_CLOUD_NAME=your-cloud-name
export CLOUDINARY_API_KEY=your-api-key
export CLOUDINARY_API_SECRET=your-api-secret
```

### 2. Error Handling
The service includes error handling for:
- Upload failures
- Transformation errors
- Network issues

### 3. Fallback Strategy
If Cloudinary is unavailable:
- Images fall back to local storage
- Application continues to function
- Errors are logged for monitoring

## Troubleshooting

### Common Issues

1. **Upload Fails**
   - Check API credentials
   - Verify file size limits
   - Ensure proper file format

2. **Images Not Loading**
   - Check Cloudinary URL format
   - Verify public ID is correct
   - Check network connectivity

3. **High Bandwidth Usage**
   - Enable auto-optimization
   - Use appropriate image sizes
   - Implement lazy loading

### Support
- **Cloudinary Documentation**: [docs.cloudinary.com](https://docs.cloudinary.com)
- **Community Forum**: [support.cloudinary.com](https://support.cloudinary.com)
- **API Reference**: [cloudinary.com/documentation](https://cloudinary.com/documentation)

## Cost Optimization

### Free Plan Limits
- 25GB storage
- 25GB bandwidth/month
- 25,000 transformations/month

### Optimization Tips
1. **Use Auto-Optimization**: Reduces file sizes automatically
2. **Implement Caching**: Cache transformed images
3. **Monitor Usage**: Track bandwidth and storage usage
4. **Clean Up**: Delete unused images regularly

### Upgrade Considerations
- **Pro Plan**: $89/month for 225GB storage, 225GB bandwidth
- **Advanced Plan**: $224/month for 500GB storage, 500GB bandwidth
- **Custom Plans**: Available for enterprise needs 