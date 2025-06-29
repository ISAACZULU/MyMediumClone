package org.example.service.impl;

import org.example.dto.TagSuggestionDto;
import org.example.entity.Article;
import org.example.entity.Tag;
import org.example.repository.ArticleRepository;
import org.example.repository.TagRepository;
import org.example.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {
    
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private ArticleRepository articleRepository;

    @Override
    public List<String> getAllTags() {
        return tagRepository.findAll().stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
    }

    @Override
    public List<TagSuggestionDto> getTagSuggestions(String query, int limit) {
        Page<Tag> tags = tagRepository.findByNameContainingIgnoreCase(query, PageRequest.of(0, limit));
        
        return tags.getContent().stream()
                .map(tag -> new TagSuggestionDto(tag.getId(), tag.getName(), tag.getArticleCount()))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getTrendingTags(int days, int limit) {
        Page<Tag> tags = tagRepository.findTrendingTags(PageRequest.of(0, limit));
        
        return tags.getContent().stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getArticlesByTag(String tag, Pageable pageable) {
        Set<String> tagSet = Set.of(tag);
        Page<Article> articles = articleRepository.findByTags(tagSet, pageable);
        
        return articles.getContent().stream()
                .map(article -> article.getSlug())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addTagToArticle(Long articleId, String tag) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        
        Tag tagEntity = tagRepository.findByName(tag)
                .orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(tag);
                    return tagRepository.save(newTag);
                });
        
        article.getTags().add(tagEntity);
        articleRepository.save(article);
    }

    @Override
    @Transactional
    public void removeTagFromArticle(Long articleId, String tag) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        
        Tag tagEntity = tagRepository.findByName(tag)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
        
        article.getTags().remove(tagEntity);
        articleRepository.save(article);
    }
}
