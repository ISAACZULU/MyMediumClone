package org.example.controller;

import org.example.dto.MediaResponseDto;
import org.example.service.MediaService;
import org.example.service.CloudinaryService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/media")
@CrossOrigin(origins = "*")
public class MediaController {
    
    @Autowired
    private MediaService mediaService;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private UserService userService;
    
    @PostMapping("/upload")
    public ResponseEntity<MediaResponseDto> uploadFile(
            @RequestParam("file") MultipartFile file,
            Principal principal) throws IOException {
        String username = principal.getName();
        // Get user ID from username (you might want to inject UserService here)
        Long userId = 1L; // Placeholder - implement proper user ID retrieval
        MediaResponseDto response = mediaService.uploadFile(file, userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{mediaId}")
    public ResponseEntity<MediaResponseDto> getMediaInfo(@PathVariable Long mediaId) {
        MediaResponseDto media = mediaService.getMediaById(mediaId);
        return ResponseEntity.ok(media);
    }
    
    @GetMapping("/{mediaId}/file")
    public ResponseEntity<ByteArrayResource> getMediaFile(@PathVariable Long mediaId) throws IOException {
        byte[] fileData = mediaService.getMediaFile(mediaId);
        ByteArrayResource resource = new ByteArrayResource(fileData);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
    
    @GetMapping("/{mediaId}/thumbnail")
    public ResponseEntity<ByteArrayResource> getMediaThumbnail(@PathVariable Long mediaId) throws IOException {
        byte[] thumbnailData = mediaService.getMediaThumbnail(mediaId);
        ByteArrayResource resource = new ByteArrayResource(thumbnailData);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MediaResponseDto>> getUserMedia(@PathVariable Long userId) {
        List<MediaResponseDto> mediaList = mediaService.getUserMedia(userId);
        return ResponseEntity.ok(mediaList);
    }
    
    @DeleteMapping("/{mediaId}")
    public ResponseEntity<Void> deleteMedia(
            @PathVariable Long mediaId,
            Principal principal) {
        String username = principal.getName();
        // Get user ID from username (you might want to inject UserService here)
        Long userId = 1L; // Placeholder - implement proper user ID retrieval
        mediaService.deleteMedia(mediaId, userId);
        return ResponseEntity.noContent().build();
    }

    // Cloudinary-specific endpoints
    @PostMapping("/cloudinary/upload")
    public ResponseEntity<MediaResponseDto> uploadToCloudinary(
            @RequestParam("file") MultipartFile file,
            Principal principal) throws IOException {
        String username = principal.getName();
        // Get user ID from username (you might want to inject UserService here)
        Long userId = 1L; // Placeholder - implement proper user ID retrieval
        MediaResponseDto response = cloudinaryService.uploadImage(file, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cloudinary/{publicId}/optimize")
    public ResponseEntity<String> getOptimizedImage(
            @PathVariable String publicId,
            @RequestParam(defaultValue = "800") int width,
            @RequestParam(defaultValue = "600") int height,
            @RequestParam(required = false) String format) {
        String optimizedUrl = cloudinaryService.generateOptimizedUrl(publicId, width, height, format);
        return ResponseEntity.ok(optimizedUrl);
    }

    @GetMapping("/cloudinary/{publicId}/responsive")
    public ResponseEntity<String> getResponsiveImage(
            @PathVariable String publicId,
            @RequestParam(defaultValue = "1200") int maxWidth) {
        String responsiveUrl = cloudinaryService.generateResponsiveUrl(publicId, maxWidth);
        return ResponseEntity.ok(responsiveUrl);
    }

    @GetMapping("/cloudinary/{publicId}/filter")
    public ResponseEntity<String> getArtisticFilter(
            @PathVariable String publicId,
            @RequestParam String filter) {
        String filteredUrl = cloudinaryService.generateArtisticFilter(publicId, filter);
        return ResponseEntity.ok(filteredUrl);
    }
} 