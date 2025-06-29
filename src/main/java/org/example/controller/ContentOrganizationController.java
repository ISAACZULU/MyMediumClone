package org.example.controller;

import org.example.dto.*;
import org.example.service.ContentOrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/organization")
@CrossOrigin(origins = "*")
public class ContentOrganizationController {
    
    @Autowired
    private ContentOrganizationService contentOrganizationService;
    
    // Tag endpoints
    @GetMapping("/tags/suggestions")
    public ResponseEntity<List<TagSuggestionDto>> getTagSuggestions(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int limit) {
        List<TagSuggestionDto> suggestions = contentOrganizationService.getTagSuggestions(query, limit);
        return ResponseEntity.ok(suggestions);
    }
    
    @GetMapping("/tags/trending")
    public ResponseEntity<Page<TagSuggestionDto>> getTrendingTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<TagSuggestionDto> trendingTags = contentOrganizationService.getTrendingTags(page, size);
        return ResponseEntity.ok(trendingTags);
    }
    
    @GetMapping("/tags/recent")
    public ResponseEntity<List<TagSuggestionDto>> getRecentlyUsedTags(
            @RequestParam(defaultValue = "10") int limit) {
        List<TagSuggestionDto> recentTags = contentOrganizationService.getRecentlyUsedTags(limit);
        return ResponseEntity.ok(recentTags);
    }
    
    @GetMapping("/tags/recommendations")
    public ResponseEntity<List<TagSuggestionDto>> getTagRecommendations(
            @RequestParam(defaultValue = "10") int limit,
            Principal principal) {
        String username = principal.getName();
        List<TagSuggestionDto> recommendations = contentOrganizationService.getTagRecommendations(username, limit);
        return ResponseEntity.ok(recommendations);
    }
    
    // Article Collection endpoints
    @PostMapping("/collections")
    public ResponseEntity<ArticleCollectionDto> createCollection(
            @RequestBody ArticleCollectionDto dto,
            Principal principal) {
        String username = principal.getName();
        ArticleCollectionDto response = contentOrganizationService.createCollection(dto, username);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/collections/{collectionId}")
    public ResponseEntity<ArticleCollectionDto> updateCollection(
            @PathVariable Long collectionId,
            @RequestBody ArticleCollectionDto dto,
            Principal principal) {
        String username = principal.getName();
        ArticleCollectionDto response = contentOrganizationService.updateCollection(collectionId, dto, username);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/collections/{collectionId}/articles/{articleId}")
    public ResponseEntity<Void> addArticleToCollection(
            @PathVariable Long collectionId,
            @PathVariable Long articleId,
            Principal principal) {
        String username = principal.getName();
        contentOrganizationService.addArticleToCollection(collectionId, articleId, username);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/collections/{collectionId}/articles/{articleId}")
    public ResponseEntity<Void> removeArticleFromCollection(
            @PathVariable Long collectionId,
            @PathVariable Long articleId,
            Principal principal) {
        String username = principal.getName();
        contentOrganizationService.removeArticleFromCollection(collectionId, articleId, username);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/collections/{collectionId}/collaborators")
    public ResponseEntity<Void> addCollaborator(
            @PathVariable Long collectionId,
            @RequestParam String collaboratorUsername,
            Principal principal) {
        String username = principal.getName();
        contentOrganizationService.addCollaborator(collectionId, collaboratorUsername, username);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/collections")
    public ResponseEntity<List<ArticleCollectionDto>> getUserCollections(Principal principal) {
        String username = principal.getName();
        List<ArticleCollectionDto> collections = contentOrganizationService.getUserCollections(username);
        return ResponseEntity.ok(collections);
    }
    
    @GetMapping("/collections/{collectionId}")
    public ResponseEntity<ArticleCollectionDto> getCollection(
            @PathVariable Long collectionId,
            Principal principal) {
        String username = principal != null ? principal.getName() : null;
        ArticleCollectionDto collection = contentOrganizationService.getCollection(collectionId, username);
        return ResponseEntity.ok(collection);
    }
    
    @GetMapping("/collections/popular")
    public ResponseEntity<Page<ArticleCollectionDto>> getPopularCollections(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ArticleCollectionDto> collections = contentOrganizationService.getPopularCollections(page, size);
        return ResponseEntity.ok(collections);
    }
    
    // Draft endpoints
    @PostMapping("/drafts")
    public ResponseEntity<DraftDto> createDraft(Principal principal) {
        String username = principal.getName();
        DraftDto draft = contentOrganizationService.createDraft(username);
        return ResponseEntity.ok(draft);
    }
    
    @PutMapping("/drafts/{draftId}")
    public ResponseEntity<DraftDto> autoSaveDraft(
            @PathVariable Long draftId,
            @RequestBody DraftDto dto,
            Principal principal) {
        String username = principal.getName();
        DraftDto response = contentOrganizationService.autoSaveDraft(draftId, dto, username);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/drafts")
    public ResponseEntity<List<DraftDto>> getUserDrafts(
            @RequestParam(defaultValue = "false") boolean includeArchived,
            Principal principal) {
        String username = principal.getName();
        List<DraftDto> drafts = contentOrganizationService.getUserDrafts(username, includeArchived);
        return ResponseEntity.ok(drafts);
    }
    
    @PostMapping("/drafts/{draftId}/archive")
    public ResponseEntity<Void> archiveDraft(
            @PathVariable Long draftId,
            Principal principal) {
        String username = principal.getName();
        contentOrganizationService.archiveDraft(draftId, username);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/drafts/{draftId}")
    public ResponseEntity<Void> deleteDraft(
            @PathVariable Long draftId,
            Principal principal) {
        String username = principal.getName();
        contentOrganizationService.deleteDraft(draftId, username);
        return ResponseEntity.noContent().build();
    }
} 