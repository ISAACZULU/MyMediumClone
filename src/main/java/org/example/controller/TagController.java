package org.example.controller;

import org.example.dto.TagSuggestionDto;
import org.example.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
@CrossOrigin(origins = "*")
public class TagController {
    
    @Autowired
    private TagService tagService;

    @GetMapping
    public ResponseEntity<List<String>> getAllTags() {
        List<String> tags = tagService.getAllTags();
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<TagSuggestionDto>> getTagSuggestions(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int limit) {
        List<TagSuggestionDto> suggestions = tagService.getTagSuggestions(query, limit);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/trending")
    public ResponseEntity<List<String>> getTrendingTags(
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "10") int limit) {
        List<String> trendingTags = tagService.getTrendingTags(days, limit);
        return ResponseEntity.ok(trendingTags);
    }

    @GetMapping("/articles/{tag}")
    public ResponseEntity<List<String>> getArticlesByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<String> articles = tagService.getArticlesByTag(tag, PageRequest.of(page, size));
        return ResponseEntity.ok(articles);
    }
}
