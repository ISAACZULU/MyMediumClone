package org.example.controller;

import org.example.dto.*;
import org.example.service.ArticleService;
import org.example.service.BookmarkService;
import org.example.service.UserService;
import org.example.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/articles")
@CrossOrigin(origins = "*")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private UserService userService;
    @Autowired
    private RecommendationService recommendationService;
    @Autowired
    private BookmarkService bookmarkService;

    @PostMapping
    public ResponseEntity<ArticleResponseDto> createArticle(@RequestBody ArticleCreateDto dto, Principal principal) {
        String username = principal.getName();
        ArticleResponseDto response = articleService.createArticle(dto, username);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponseDto> editArticle(@PathVariable Long id, @RequestBody ArticleEditDto dto, Principal principal) {
        String username = principal.getName();
        ArticleResponseDto response = articleService.editArticle(id, dto, username);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id, Principal principal) {
        String username = principal.getName();
        articleService.deleteArticle(id, username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ArticleResponseDto> getArticleBySlug(@PathVariable String slug, Principal principal) {
        String username = principal != null ? principal.getName() : null;
        ArticleResponseDto response = articleService.getArticleBySlug(slug, username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/versions")
    public ResponseEntity<List<ArticleVersionDto>> getArticleVersions(@PathVariable Long id) {
        List<ArticleVersionDto> versions = articleService.getArticleVersions(id);
        return ResponseEntity.ok(versions);
    }

    @GetMapping("/feed")
    public ResponseEntity<Page<ArticleResponseDto>> getFeed(@RequestParam List<Long> followedUserIds, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<ArticleResponseDto> feed = articleService.getFeed(followedUserIds, page, size);
        return ResponseEntity.ok(feed);
    }

    @GetMapping("/trending")
    public ResponseEntity<Page<ArticleResponseDto>> getTrending(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<ArticleResponseDto> trending = articleService.getTrending(page, size);
        return ResponseEntity.ok(trending);
    }

    @PostMapping("/{articleId}/bookmark")
    public ResponseEntity<Void> bookmarkArticle(
            @PathVariable Long articleId,
            Principal principal) {
        String username = principal.getName();
        bookmarkService.addBookmark(articleId, username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{articleId}/bookmark")
    public ResponseEntity<Void> removeBookmark(
            @PathVariable Long articleId,
            Principal principal) {
        String username = principal.getName();
        bookmarkService.removeBookmark(articleId, username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{articleId}/bookmarks")
    public ResponseEntity<List<BookmarkCollectionDto>> getArticleBookmarks(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<BookmarkCollectionDto> bookmarks = bookmarkService.getBookmarksForArticle(articleId, 
            org.springframework.data.domain.PageRequest.of(page, size));
        return ResponseEntity.ok(bookmarks);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ArticleResponseDto>> searchArticles(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Set<String> tags,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ArticleResponseDto> results = articleService.searchArticles(keyword, tags, author, startDate, endDate, page, size);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/cursor")
    public ResponseEntity<CursorPage<ArticleResponseDto>> getArticlesCursorPage(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int size) {
        CursorPage<ArticleResponseDto> page = articleService.getArticlesCursorPage(cursor, size);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{articleId}/recommendations")
    public ResponseEntity<List<ArticleResponseDto>> getMoreLikeThis(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "10") int limit,
            Principal principal) {
        String username = principal != null ? principal.getName() : null;
        List<ArticleResponseDto> recommendations = recommendationService.getMoreLikeThis(articleId, username, limit);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/feed/personalized")
    public ResponseEntity<List<ArticleResponseDto>> getPersonalizedFeed(
            @RequestParam(defaultValue = "10") int limit,
            Principal principal) {
        String username = principal.getName();
        List<ArticleResponseDto> feed = recommendationService.getPersonalizedFeed(username, limit);
        return ResponseEntity.ok(feed);
    }
} 