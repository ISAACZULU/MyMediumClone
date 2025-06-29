package org.example.controller;

import org.example.dto.*;
import org.example.service.EngagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/engagement")
@CrossOrigin(origins = "*")
public class EngagementController {
    
    @Autowired
    private EngagementService engagementService;
    
    // Clap endpoints
    @PostMapping("/articles/{articleId}/clap")
    public ResponseEntity<ClapDto> clapArticle(
            @PathVariable Long articleId,
            @RequestParam Integer clapCount,
            Principal principal) {
        String username = principal.getName();
        ClapDto response = engagementService.clapArticle(articleId, clapCount, username);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/articles/{articleId}/clap")
    public ResponseEntity<ClapDto> getClapInfo(
            @PathVariable Long articleId,
            Principal principal) {
        String username = principal != null ? principal.getName() : null;
        ClapDto response = engagementService.getClapInfo(articleId, username);
        return ResponseEntity.ok(response);
    }
    
    // Comment endpoints
    @PostMapping("/articles/{articleId}/comments")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long articleId,
            @RequestBody CommentCreateDto dto,
            Principal principal) {
        String username = principal.getName();
        CommentResponseDto response = engagementService.createComment(articleId, dto, username);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long commentId,
            @RequestParam String content,
            Principal principal) {
        String username = principal.getName();
        CommentResponseDto response = engagementService.updateComment(commentId, content, username);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            Principal principal) {
        String username = principal.getName();
        engagementService.deleteComment(commentId, username);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/comments/{commentId}/flag")
    public ResponseEntity<Void> flagComment(
            @PathVariable Long commentId,
            Principal principal) {
        String username = principal.getName();
        engagementService.flagComment(commentId, username);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/articles/{articleId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getArticleComments(@PathVariable Long articleId) {
        List<CommentResponseDto> comments = engagementService.getArticleComments(articleId);
        return ResponseEntity.ok(comments);
    }
    
    @GetMapping("/articles/{articleId}/comments/cursor")
    public ResponseEntity<CursorPage<CommentResponseDto>> getArticleCommentsCursorPage(
            @PathVariable Long articleId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int size) {
        CursorPage<CommentResponseDto> page = engagementService.getArticleCommentsCursorPage(articleId, cursor, size);
        return ResponseEntity.ok(page);
    }
    
    // Bookmark endpoints
    @PostMapping("/articles/{articleId}/bookmark")
    public ResponseEntity<Void> bookmarkArticle(
            @PathVariable Long articleId,
            @RequestParam(required = false) Long collectionId,
            Principal principal) {
        String username = principal.getName();
        engagementService.bookmarkArticle(articleId, collectionId, username);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/articles/{articleId}/bookmark")
    public ResponseEntity<Void> removeBookmark(
            @PathVariable Long articleId,
            Principal principal) {
        String username = principal.getName();
        engagementService.removeBookmark(articleId, username);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/collections")
    public ResponseEntity<BookmarkCollectionDto> createCollection(
            @RequestBody BookmarkCollectionDto dto,
            Principal principal) {
        String username = principal.getName();
        BookmarkCollectionDto response = engagementService.createCollection(dto, username);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/collections")
    public ResponseEntity<List<BookmarkCollectionDto>> getUserCollections(Principal principal) {
        String username = principal.getName();
        List<BookmarkCollectionDto> collections = engagementService.getUserCollections(username);
        return ResponseEntity.ok(collections);
    }
    
    @GetMapping("/collections/{collectionId}")
    public ResponseEntity<BookmarkCollectionDto> getCollection(
            @PathVariable Long collectionId,
            Principal principal) {
        String username = principal != null ? principal.getName() : null;
        BookmarkCollectionDto collection = engagementService.getCollection(collectionId, username);
        return ResponseEntity.ok(collection);
    }
    
    // Sharing endpoints
    @GetMapping("/articles/{slug}/share")
    public ResponseEntity<ShareDto> getShareInfo(@PathVariable String slug) {
        ShareDto shareInfo = engagementService.generateShareInfo(slug);
        return ResponseEntity.ok(shareInfo);
    }
} 