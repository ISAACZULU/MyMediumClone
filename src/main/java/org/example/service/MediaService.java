package org.example.service;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.example.dto.MediaResponseDto;
import org.example.entity.Media;
import org.example.entity.User;
import org.example.repository.MediaRepository;
import org.example.repository.UserRepository;
import org.example.config.CdnConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.example.exception.ResourceNotFoundException;
import org.example.exception.ValidationException;
import org.example.exception.ForbiddenException;

@Service
@RequiredArgsConstructor
public class MediaService {
    
    private static final Logger logger = LoggerFactory.getLogger(MediaService.class);
    
    @Value("${app.media.upload.path}")
    private String uploadPath;
    
    @Value("${app.media.max-file-size}")
    private long maxFileSize;
    
    @Value("${app.media.allowed-image-types}")
    private String allowedImageTypes;
    
    @Value("${app.media.compression.quality}")
    private double compressionQuality;
    
    @Value("${app.media.compression.max-width}")
    private int maxWidth;
    
    @Value("${app.media.compression.max-height}")
    private int maxHeight;
    
    @Value("${app.media.thumbnail.width}")
    private int thumbnailWidth;
    
    @Value("${app.media.thumbnail.height}")
    private int thumbnailHeight;
    
    @Value("${app.cdn.base-url}")
    private String cdnBaseUrl;
    
    @Value("${app.cdn.enabled}")
    private boolean cdnEnabled;
    
    private final MediaRepository mediaRepository;
    private final UserRepository userRepository;
    private final CdnConfig cdnConfig;
    
    public MediaResponseDto uploadFile(MultipartFile file, Long userId) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Validate file
        validateFile(file);
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String storedFilename = UUID.randomUUID().toString() + fileExtension;
        
        // Create upload directory
        Path uploadDir = Paths.get(uploadPath, String.valueOf(userId));
        Files.createDirectories(uploadDir);
        
        // Save original file
        Path filePath = uploadDir.resolve(storedFilename);
        Files.copy(file.getInputStream(), filePath);
        
        // Generate file hash
        String hash = generateFileHash(filePath);
        
        // Check for duplicate
        mediaRepository.findByHash(hash).ifPresent(existingMedia -> {
            try {
                Files.delete(filePath);
            } catch (IOException e) {
                logger.error("Failed to delete duplicate file", e);
            }
            throw new RuntimeException("File already exists");
        });
        
        // Create media entity
        Media media = new Media(originalFilename, storedFilename, file.getContentType(),
                file.getSize(), filePath.toString(), hash, user);
        
        // Process image if it's an image file
        if (isImageFile(file.getContentType())) {
            processImage(media, filePath);
        }
        
        media = mediaRepository.save(media);
        
        return convertToDto(media);
    }
    
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ValidationException("File is empty");
        }
        if (file.getSize() > maxFileSize) {
            throw new ValidationException("File size exceeds maximum allowed size");
        }
        String contentType = file.getContentType();
        if (contentType == null || !isAllowedFileType(contentType)) {
            throw new ValidationException("File type not allowed");
        }
    }
    
    private boolean isAllowedFileType(String contentType) {
        List<String> allowedTypes = Arrays.asList(allowedImageTypes.split(","));
        return allowedTypes.stream()
                .anyMatch(type -> contentType.toLowerCase().contains(type.toLowerCase()));
    }
    
    private boolean isImageFile(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
    
    private String generateFileHash(Path filePath) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(Files.readAllBytes(filePath));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new ValidationException("Failed to generate file hash");
        }
    }
    
    private void processImage(Media media, Path originalFilePath) throws IOException {
        Path uploadDir = originalFilePath.getParent();
        String filename = originalFilePath.getFileName().toString();
        String nameWithoutExt = filename.substring(0, filename.lastIndexOf("."));
        
        // Compress original image
        Path compressedPath = uploadDir.resolve(nameWithoutExt + "_compressed.jpg");
        Thumbnails.of(originalFilePath.toFile())
                .size(maxWidth, maxHeight)
                .outputQuality(compressionQuality)
                .outputFormat("jpg")
                .toFile(compressedPath.toFile());
        
        // Create thumbnail
        Path thumbnailPath = uploadDir.resolve(nameWithoutExt + "_thumb.jpg");
        Thumbnails.of(originalFilePath.toFile())
                .size(thumbnailWidth, thumbnailHeight)
                .outputQuality(0.8)
                .outputFormat("jpg")
                .toFile(thumbnailPath.toFile());
        
        // Update media entity
        media.setFilePath(compressedPath.toString());
        media.setThumbnailPath(thumbnailPath.toString());
        media.setProcessed(true);
        media.setProcessedAt(LocalDateTime.now());
        
        // Get image dimensions
        try {
            java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(compressedPath.toFile());
            media.setWidth(img.getWidth());
            media.setHeight(img.getHeight());
        } catch (Exception e) {
            logger.error("Failed to get image dimensions", e);
        }
        
        // Delete original file
        Files.delete(originalFilePath);
    }
    
    @Cacheable(value = "media", key = "#mediaId")
    public MediaResponseDto getMediaById(Long mediaId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Media not found"));
        return convertToDto(media);
    }
    
    public List<MediaResponseDto> getUserMedia(Long userId) {
        List<Media> mediaList = mediaRepository.findProcessedByUserId(userId);
        return mediaList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public void deleteMedia(Long mediaId, Long userId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Media not found"));
        
        if (!media.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this media");
        }
        
        // Delete files
        try {
            Files.deleteIfExists(Paths.get(media.getFilePath()));
            if (media.getThumbnailPath() != null) {
                Files.deleteIfExists(Paths.get(media.getThumbnailPath()));
            }
        } catch (IOException e) {
            logger.error("Failed to delete media files", e);
        }
        
        mediaRepository.delete(media);
    }
    
    private MediaResponseDto convertToDto(Media media) {
        String url = cdnConfig.getMediaUrl(media.getStoredFilename());
        String thumbnailUrl = media.getThumbnailPath() != null ? 
                cdnConfig.getThumbnailUrl(media.getStoredFilename()) : null;
        
        return new MediaResponseDto(
                media.getId(),
                media.getOriginalFilename(),
                media.getContentType(),
                media.getFileSize(),
                url,
                thumbnailUrl,
                media.getWidth(),
                media.getHeight(),
                media.getUploadedAt(),
                media.isProcessed()
        );
    }
    
    public byte[] getMediaFile(Long mediaId) throws IOException {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException("Media not found"));
        Path filePath = Paths.get(media.getFilePath());
        if (!Files.exists(filePath)) {
            throw new ResourceNotFoundException("Media file not found");
        }
        return Files.readAllBytes(filePath);
    }
    
    public byte[] getMediaThumbnail(Long mediaId) throws IOException {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException("Media not found"));
        if (media.getThumbnailPath() == null) {
            throw new ResourceNotFoundException("Thumbnail not available");
        }
        Path thumbnailPath = Paths.get(media.getThumbnailPath());
        if (!Files.exists(thumbnailPath)) {
            throw new ResourceNotFoundException("Thumbnail file not found");
        }
        return Files.readAllBytes(thumbnailPath);
    }
} 