package org.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CdnConfig {
    
    @Value("${app.cdn.enabled:false}")
    private boolean cdnEnabled;
    
    @Value("${app.cdn.base-url:}")
    private String cdnBaseUrl;
    
    @Value("${app.cdn.provider:local}")
    private String cdnProvider;
    
    @Value("${app.cdn.region:us-east-1}")
    private String cdnRegion;
    
    @Value("${app.cdn.bucket:}")
    private String cdnBucket;
    
    @Value("${app.cdn.access-key:}")
    private String cdnAccessKey;
    
    @Value("${app.cdn.secret-key:}")
    private String cdnSecretKey;
    
    // CDN URL generation methods
    public String getMediaUrl(String filename) {
        if (!cdnEnabled || cdnBaseUrl.isEmpty()) {
            return "/api/v1/media/file/" + filename;
        }
        return cdnBaseUrl + "/media/" + filename;
    }
    
    public String getThumbnailUrl(String filename) {
        if (!cdnEnabled || cdnBaseUrl.isEmpty()) {
            return "/api/v1/media/thumbnail/" + filename;
        }
        return cdnBaseUrl + "/thumbnails/" + filename;
    }
    
    public String getOptimizedUrl(String filename, int width, int height) {
        if (!cdnEnabled || cdnBaseUrl.isEmpty()) {
            return "/api/v1/media/file/" + filename;
        }
        // CDN-specific image optimization parameters
        switch (cdnProvider.toLowerCase()) {
            case "cloudfront":
                return cdnBaseUrl + "/media/" + filename + "?w=" + width + "&h=" + height;
            case "cloudinary":
                return cdnBaseUrl + "/image/upload/w_" + width + ",h_" + height + "/" + filename;
            case "imgix":
                return cdnBaseUrl + "/" + filename + "?w=" + width + "&h=" + height + "&fit=crop";
            default:
                return cdnBaseUrl + "/media/" + filename;
        }
    }
    
    // Getters
    public boolean isCdnEnabled() { return cdnEnabled; }
    public String getCdnBaseUrl() { return cdnBaseUrl; }
    public String getCdnProvider() { return cdnProvider; }
    public String getCdnRegion() { return cdnRegion; }
    public String getCdnBucket() { return cdnBucket; }
    public String getCdnAccessKey() { return cdnAccessKey; }
    public String getCdnSecretKey() { return cdnSecretKey; }
} 