package org.example.service;

import org.example.dto.TagSuggestionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TagService {
    List<String> getAllTags();
    List<TagSuggestionDto> getTagSuggestions(String query, int limit);
    List<String> getTrendingTags(int days, int limit);
    List<String> getArticlesByTag(String tag, Pageable pageable);
    void addTagToArticle(Long articleId, String tag);
    void removeTagFromArticle(Long articleId, String tag);
}
