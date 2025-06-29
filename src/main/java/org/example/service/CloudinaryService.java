package org.example.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.example.dto.MediaResponseDto;
import org.example.entity.Media;
import org.example.entity.User;
import org.example.repository.MediaRepository;
import org.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import com.cloudinary.Transformation;

@Service
public class CloudinaryService {
    
    private static final Logger logger = LoggerFactory.getLogger(CloudinaryService.class);
    
    @Autowired
    private Cloudinary cloudinary;
    
    @Autowired
    private MediaRepository mediaRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Value("${app.cloudinary.folder:medium-clone}")
    private String cloudinaryFolder;
    
    @Value("${app.cloudinary.auto-optimize:true}")
    private boolean autoOptimize;
    
    public MediaResponseDto uploadImage(MultipartFile file, Long userId) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String publicId = cloudinaryFolder + "/" + UUID.randomUUID().toString();
        
        // Upload options for Cloudinary
        Map<String, Object> uploadOptions = ObjectUtils.asMap(
            "public_id", publicId,
            "folder", cloudinaryFolder,
            "resource_type", "image",
            "transformation", getDefaultTransformation()
        );
        
        // Upload to Cloudinary
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
        
        // Extract Cloudinary URLs
        String secureUrl = (String) uploadResult.get("secure_url");
        String publicIdResult = (String) uploadResult.get("public_id");
        Integer width = (Integer) uploadResult.get("width");
        Integer height = (Integer) uploadResult.get("height");
        Long fileSize = (Long) uploadResult.get("bytes");
        
        // Create thumbnail URL
        String thumbnailUrl = generateThumbnailUrl(publicIdResult);
        
        // Save to database
        Media media = new Media();
        media.setOriginalFilename(originalFilename);
        media.setStoredFilename(publicIdResult);
        media.setContentType(file.getContentType());
        media.setFileSize(fileSize);
        media.setFilePath(secureUrl);
        media.setThumbnailPath(thumbnailUrl);
        media.setWidth(width);
        media.setHeight(height);
        media.setHash(generateHash(publicIdResult));
        media.setUser(user);
        media.setUploadedAt(LocalDateTime.now());
        media.setProcessed(true);
        media.setProcessedAt(LocalDateTime.now());
        media.setCdnUrl(secureUrl);
        
        media = mediaRepository.save(media);
        
        return convertToDto(media);
    }
    
    public String generateOptimizedUrl(String publicId, int width, int height, String format) {
        try {
            Transformation transformation = new Transformation()
                .width(width)
                .height(height)
                .crop("fill")
                .quality("auto");
            
            if (format != null) {
                transformation = transformation.fetchFormat(format);
            }
            
            return cloudinary.url()
                    .transformation(transformation)
                    .generate(publicId);
        } catch (Exception e) {
            logger.error("Error generating optimized URL for publicId: " + publicId, e);
            return cloudinary.url().generate(publicId);
        }
    }
    
    public String generateThumbnailUrl(String publicId) {
        try {
            Transformation transformation = new Transformation()
                .width(300)
                .height(200)
                .crop("fill")
                .quality("auto")
                .fetchFormat("auto");
            
            return cloudinary.url()
                    .transformation(transformation)
                    .generate(publicId);
        } catch (Exception e) {
            logger.error("Error generating thumbnail URL for publicId: " + publicId, e);
            return cloudinary.url().generate(publicId);
        }
    }
    
    public void deleteImage(String publicId) {
        try {
            Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            logger.info("Deleted image from Cloudinary: " + publicId + ", result: " + result);
        } catch (Exception e) {
            logger.error("Error deleting image from Cloudinary: " + publicId, e);
        }
    }
    
    public String generateResponsiveUrl(String publicId, int maxWidth) {
        try {
            Transformation transformation = new Transformation()
                .width("auto")
                .crop("scale")
                .quality("auto")
                .fetchFormat("auto");
            
            // Set max width using a different approach
            String url = cloudinary.url()
                    .transformation(transformation)
                    .generate(publicId);
            
            // Add max width parameter manually
            return url + "?max_width=" + maxWidth;
        } catch (Exception e) {
            logger.error("Error generating responsive URL for publicId: " + publicId, e);
            return cloudinary.url().generate(publicId);
        }
    }
    
    public String generateArtisticFilter(String publicId, String filter) {
        try {
            Transformation transformation = new Transformation()
                .effect(filter)
                .quality("auto");
            
            return cloudinary.url()
                    .transformation(transformation)
                    .generate(publicId);
        } catch (Exception e) {
            logger.error("Error generating artistic filter URL for publicId: " + publicId, e);
            return cloudinary.url().generate(publicId);
        }
    }
    
    private Map<String, Object> getDefaultTransformation() {
        if (autoOptimize) {
            return ObjectUtils.asMap(
                "quality", "auto",
                "format", "auto",
                "fetch_format", "auto"
            );
        }
        return ObjectUtils.emptyMap();
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
    
    private String generateHash(String publicId) {
        return UUID.nameUUIDFromBytes(publicId.getBytes()).toString();
    }
    
    private MediaResponseDto convertToDto(Media media) {
        return new MediaResponseDto(
                media.getId(),
                media.getOriginalFilename(),
                media.getContentType(),
                media.getFileSize(),
                media.getCdnUrl(),
                media.getThumbnailPath(),
                media.getWidth(),
                media.getHeight(),
                media.getUploadedAt(),
                media.isProcessed()
        );
    }
} 