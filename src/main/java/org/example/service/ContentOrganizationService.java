package org.example.service;

import org.example.dto.*;
import org.example.entity.*;
import org.example.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.exception.ResourceNotFoundException;
import org.example.exception.ForbiddenException;
import org.example.exception.NotAllowedException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentOrganizationService {
    
    private final TagRepository tagRepository;
    
    private final ArticleCollectionRepository articleCollectionRepository;
    
    private final DraftRepository draftRepository;
    
    private final UserRepository userRepository;
    
    private final ArticleRepository articleRepository;
    
    // Tag functionality
    public List<TagSuggestionDto> getTagSuggestions(String query, int limit) {
        List<Tag> tags = tagRepository.findByNameContainingIgnoreCaseOrderByArticleCountDesc(query);
        return tags.stream()
                .limit(limit)
                .map(this::toTagSuggestionDto)
                .collect(Collectors.toList());
    }
    
    public Page<TagSuggestionDto> getTrendingTags(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return tagRepository.findTrendingTags(pageable)
                .map(this::toTagSuggestionDto);
    }
    
    public List<TagSuggestionDto> getRecentlyUsedTags(int limit) {
        LocalDateTime since = LocalDateTime.now().minusWeeks(1);
        List<Tag> tags = tagRepository.findRecentlyUsedTags(since);
        return tags.stream()
                .limit(limit)
                .map(this::toTagSuggestionDto)
                .collect(Collectors.toList());
    }
    
    public List<TagSuggestionDto> getTagRecommendations(String username, int limit) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Get user's recent tags and find similar ones
        List<Tag> userTags = tagRepository.findRecentlyActiveTags();
        List<Tag> recommendations = tagRepository.findMostUsedTags(PageRequest.of(0, limit * 2))
                .getContent();
        
        // Filter out tags user already uses frequently
        Set<String> userTagNames = userTags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
        
        return recommendations.stream()
                .filter(tag -> !userTagNames.contains(tag.getName()))
                .limit(limit)
                .map(this::toTagSuggestionDto)
                .collect(Collectors.toList());
    }
    
    @Scheduled(cron = "0 0 2 * * ?") // Run daily at 2 AM
    @Transactional
    public void updateTrendingScores() {
        List<Tag> tags = tagRepository.findAll();
        LocalDateTime weekAgo = LocalDateTime.now().minusWeeks(1);
        LocalDateTime monthAgo = LocalDateTime.now().minusMonths(1);
        
        for (Tag tag : tags) {
            // Calculate trending score based on recent usage vs total usage
            double recentUsage = tag.getWeeklyUsage().doubleValue();
            double totalUsage = tag.getArticleCount().doubleValue();
            double trendingScore = totalUsage > 0 ? (recentUsage / totalUsage) * 100 : 0;
            
            tag.setTrendingScore(trendingScore);
            tagRepository.save(tag);
        }
    }
    
    // Article Collection functionality
    @Transactional
    public ArticleCollectionDto createCollection(ArticleCollectionDto dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        ArticleCollection collection = new ArticleCollection(
                dto.getName(), dto.getDescription(), dto.isPublic(), dto.isCollaborative(), user);
        
        // Handle tags
        if (dto.getTags() != null) {
            Set<Tag> tags = new HashSet<>();
            for (String tagName : dto.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName)));
                tags.add(tag);
            }
            collection.setTags(tags);
        }
        
        ArticleCollection saved = articleCollectionRepository.save(collection);
        return toArticleCollectionDto(saved);
    }
    
    @Transactional
    public ArticleCollectionDto updateCollection(Long collectionId, ArticleCollectionDto dto, String username) {
        ArticleCollection collection = articleCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!collection.canEdit(user)) {
            throw new ForbiddenException("You don't have permission to edit this collection");
        }
        
        if (dto.getName() != null) collection.setName(dto.getName());
        if (dto.getDescription() != null) collection.setDescription(dto.getDescription());
        collection.setPublic(dto.isPublic());
        collection.setCollaborative(dto.isCollaborative());
        
        // Handle tags
        if (dto.getTags() != null) {
            Set<Tag> tags = new HashSet<>();
            for (String tagName : dto.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName)));
                tags.add(tag);
            }
            collection.setTags(tags);
        }
        
        ArticleCollection saved = articleCollectionRepository.save(collection);
        return toArticleCollectionDto(saved);
    }
    
    @Transactional
    public void addArticleToCollection(Long collectionId, Long articleId, String username) {
        ArticleCollection collection = articleCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));
        
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!collection.canEdit(user)) {
            throw new ForbiddenException("You don't have permission to edit this collection");
        }
        
        collection.addArticle(article);
        articleCollectionRepository.save(collection);
    }
    
    @Transactional
    public void removeArticleFromCollection(Long collectionId, Long articleId, String username) {
        ArticleCollection collection = articleCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));
        
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!collection.canEdit(user)) {
            throw new ForbiddenException("You don't have permission to edit this collection");
        }
        
        collection.removeArticle(article);
        articleCollectionRepository.save(collection);
    }
    
    @Transactional
    public void addCollaborator(Long collectionId, String collaboratorUsername, String username) {
        ArticleCollection collection = articleCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        User collaborator = userRepository.findByUsername(collaboratorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Collaborator not found"));
        
        if (!collection.getOwner().equals(user)) {
            throw new ForbiddenException("Only the owner can add collaborators");
        }
        
        collection.addCollaborator(collaborator);
        articleCollectionRepository.save(collection);
    }
    
    public List<ArticleCollectionDto> getUserCollections(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        List<ArticleCollection> collections = articleCollectionRepository.findByOwner(user);
        List<ArticleCollection> collaborativeCollections = articleCollectionRepository.findByCollaborator(user);
        
        Set<ArticleCollection> allCollections = new HashSet<>();
        allCollections.addAll(collections);
        allCollections.addAll(collaborativeCollections);
        
        return allCollections.stream()
                .map(this::toArticleCollectionDto)
                .collect(Collectors.toList());
    }
    
    public ArticleCollectionDto getCollection(Long collectionId, String username) {
        ArticleCollection collection = articleCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));
        
        if (!collection.isPublic()) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            if (!collection.canEdit(user)) {
                throw new NotAllowedException("This collection is private");
            }
        }
        
        return toArticleCollectionDtoWithDetails(collection);
    }
    
    public Page<ArticleCollectionDto> getPopularCollections(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return articleCollectionRepository.findPopularCollections(pageable)
                .map(this::toArticleCollectionDto);
    }
    
    // Draft functionality
    @Transactional
    public DraftDto createDraft(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Draft draft = new Draft(user);
        Draft saved = draftRepository.save(draft);
        
        return toDraftDto(saved);
    }
    
    @Transactional
    public DraftDto autoSaveDraft(Long draftId, DraftDto dto, String username) {
        Draft draft = draftRepository.findById(draftId)
                .orElseThrow(() -> new ResourceNotFoundException("Draft not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!draft.getAuthor().equals(user)) {
            throw new ForbiddenException("You can only edit your own drafts");
        }
        
        if (dto.getTitle() != null) draft.setTitle(dto.getTitle());
        if (dto.getContent() != null) draft.setContent(dto.getContent());
        if (dto.getSummary() != null) draft.setSummary(dto.getSummary());
        if (dto.getCoverImageUrl() != null) draft.setCoverImageUrl(dto.getCoverImageUrl());
        
        // Handle tags
        if (dto.getTags() != null) {
            Set<Tag> tags = new HashSet<>();
            for (String tagName : dto.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName)));
                tags.add(tag);
            }
            draft.setTags(tags);
        }
        
        Draft saved = draftRepository.save(draft);
        return toDraftDto(saved);
    }
    
    public List<DraftDto> getUserDrafts(String username, boolean includeArchived) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        List<Draft> drafts;
        if (includeArchived) {
            drafts = draftRepository.findByAuthor(user);
        } else {
            drafts = draftRepository.findByAuthorAndArchivedFalse(user);
        }
        
        return drafts.stream()
                .map(this::toDraftDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void archiveDraft(Long draftId, String username) {
        Draft draft = draftRepository.findById(draftId)
                .orElseThrow(() -> new ResourceNotFoundException("Draft not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!draft.getAuthor().equals(user)) {
            throw new ForbiddenException("You can only archive your own drafts");
        }
        
        draft.archive();
        draftRepository.save(draft);
    }
    
    @Transactional
    public void deleteDraft(Long draftId, String username) {
        Draft draft = draftRepository.findById(draftId)
                .orElseThrow(() -> new ResourceNotFoundException("Draft not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!draft.getAuthor().equals(user)) {
            throw new ForbiddenException("You can only delete your own drafts");
        }
        
        draftRepository.delete(draft);
    }
    
    // Helper methods
    private TagSuggestionDto toTagSuggestionDto(Tag tag) {
        TagSuggestionDto dto = new TagSuggestionDto();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        dto.setDescription(tag.getDescription());
        dto.setArticleCount(tag.getArticleCount());
        dto.setTrendingScore(tag.getTrendingScore());
        dto.setTrending(tag.getTrendingScore() > 50.0); // Consider trending if score > 50
        return dto;
    }
    
    private ArticleCollectionDto toArticleCollectionDto(ArticleCollection collection) {
        ArticleCollectionDto dto = new ArticleCollectionDto();
        dto.setId(collection.getId());
        dto.setName(collection.getName());
        dto.setDescription(collection.getDescription());
        dto.setPublic(collection.isPublic());
        dto.setCollaborative(collection.isCollaborative());
        dto.setCreatedAt(collection.getCreatedAt());
        dto.setUpdatedAt(collection.getUpdatedAt());
        dto.setOwner(new UserProfileDto(
                collection.getOwner().getId(),
                collection.getOwner().getUsername(),
                collection.getOwner().getEmail(),
                collection.getOwner().getBio(),
                collection.getOwner().getProfileImageUrl(),
                collection.getOwner().getCreatedAt()
        ));
        dto.setArticleCount(collection.getArticleCount());
        dto.setCollaboratorCount(collection.getCollaboratorCount());
        dto.setTags(collection.getTags().stream().map(Tag::getName).collect(Collectors.toSet()));
        
        return dto;
    }
    
    private ArticleCollectionDto toArticleCollectionDtoWithDetails(ArticleCollection collection) {
        ArticleCollectionDto dto = toArticleCollectionDto(collection);
        
        // Add articles
        List<ArticleResponseDto> articles = collection.getArticles().stream()
                .map(article -> {
                    ArticleResponseDto articleDto = new ArticleResponseDto();
                    articleDto.setId(article.getId());
                    articleDto.setTitle(article.getTitle());
                    articleDto.setSlug(article.getSlug());
                    articleDto.setSummary(article.getSummary());
                    articleDto.setCoverImageUrl(article.getCoverImageUrl());
                    articleDto.setReadTimeMinutes(article.getReadTimeMinutes());
                    articleDto.setCreatedAt(article.getCreatedAt());
                    articleDto.setAuthor(new UserProfileDto(
                            article.getAuthor().getId(),
                            article.getAuthor().getUsername(),
                            article.getAuthor().getEmail(),
                            article.getAuthor().getBio(),
                            article.getAuthor().getProfileImageUrl(),
                            article.getAuthor().getCreatedAt()
                    ));
                    return articleDto;
                })
                .collect(Collectors.toList());
        dto.setArticles(articles);
        
        // Add collaborators
        List<UserProfileDto> collaborators = collection.getCollaborators().stream()
                .map(user -> new UserProfileDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getBio(),
                        user.getProfileImageUrl(),
                        user.getCreatedAt()
                ))
                .collect(Collectors.toList());
        dto.setCollaborators(collaborators);
        
        return dto;
    }
    
    private DraftDto toDraftDto(Draft draft) {
        DraftDto dto = new DraftDto();
        dto.setId(draft.getId());
        dto.setTitle(draft.getTitle());
        dto.setContent(draft.getContent());
        dto.setSummary(draft.getSummary());
        dto.setCoverImageUrl(draft.getCoverImageUrl());
        dto.setAutoSavedAt(draft.getAutoSavedAt());
        dto.setCreatedAt(draft.getCreatedAt());
        dto.setUpdatedAt(draft.getUpdatedAt());
        dto.setAuthor(new UserProfileDto(
                draft.getAuthor().getId(),
                draft.getAuthor().getUsername(),
                draft.getAuthor().getEmail(),
                draft.getAuthor().getBio(),
                draft.getAuthor().getProfileImageUrl(),
                draft.getAuthor().getCreatedAt()
        ));
        dto.setTags(draft.getTags().stream().map(Tag::getName).collect(Collectors.toSet()));
        dto.setArchived(draft.isArchived());
        dto.setWordCount(draft.getWordCount());
        dto.setHasContent(draft.hasContent());
        
        return dto;
    }
} 