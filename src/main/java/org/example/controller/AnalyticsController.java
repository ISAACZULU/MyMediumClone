package org.example.controller;

import org.example.dto.ArticleAnalyticsDto;
import org.example.dto.ReadingHistoryDto;
import org.example.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    @GetMapping("/reading-history")
    public ResponseEntity<List<ReadingHistoryDto>> getUserReadingHistory(Principal principal) {
        String username = principal.getName();
        List<ReadingHistoryDto> history = analyticsService.getUserReadingHistory(username);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/articles/{articleId}")
    public ResponseEntity<ArticleAnalyticsDto> getArticleAnalytics(@PathVariable Long articleId) {
        ArticleAnalyticsDto analytics = analyticsService.getArticleAnalytics(articleId);
        return ResponseEntity.ok(analytics);
    }
    
    @GetMapping("/authors/{authorUsername}")
    public ResponseEntity<List<ArticleAnalyticsDto>> getAuthorAnalytics(@PathVariable String authorUsername) {
        List<ArticleAnalyticsDto> analytics = analyticsService.getAuthorAnalytics(authorUsername);
        return ResponseEntity.ok(analytics);
    }
} 