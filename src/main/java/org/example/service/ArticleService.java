package org.example.service;

import org.example.dto.*;
import org.example.entity.*;
import org.example.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.exception.ResourceNotFoundException;
import org.example.exception.ForbiddenException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final ArticleVersionRepository articleVersionRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final UserRepository userRepository;
    private final ReadingHistoryRepository readingHistoryRepository;
    private final ArticleAnalyticsRepository articleAnalyticsRepository;
    private final ShareRepository shareRepository;
    private final ArticleCollectionRepository articleCollectionRepository;

    @Transactional
    public ArticleResponseDto createArticle(ArticleCreateDto dto, String authorUsername) {
        User author = userRepository.findByUsername(authorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setSummary(dto.getSummary());
        article.setCoverImageUrl(dto.getCoverImageUrl());
        article.setPublished(dto.isPublished());
        article.setAuthor(author);
        // Handle tags
        Set<Tag> tags = new HashSet<>();
        if (dto.getTags() != null) {
            for (String tagName : dto.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName)));
                tags.add(tag);
            }
        }
        article.setTags(tags);
        Article saved = articleRepository.save(article);
        // Create initial version
        ArticleVersion version = new ArticleVersion(1, saved.getTitle(), saved.getContent(), saved, author);
        articleVersionRepository.save(version);
        return toResponseDto(saved, author, false);
    }

    @Transactional
    public ArticleResponseDto editArticle(Long articleId, ArticleEditDto dto, String editorUsername) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));
        User editor = userRepository.findByUsername(editorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        boolean changed = false;
        if (dto.getTitle() != null) { article.setTitle(dto.getTitle()); changed = true; }
        if (dto.getContent() != null) { article.setContent(dto.getContent()); changed = true; }
        if (dto.getSummary() != null) { article.setSummary(dto.getSummary()); changed = true; }
        if (dto.getCoverImageUrl() != null) { article.setCoverImageUrl(dto.getCoverImageUrl()); changed = true; }
        if (dto.getTags() != null) {
            Set<Tag> tags = new HashSet<>();
            for (String tagName : dto.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName)));
                tags.add(tag);
            }
            article.setTags(tags);
            changed = true;
        }
        if (dto.isPublished() != article.isPublished()) {
            article.setPublished(dto.isPublished());
            changed = true;
        }
        if (changed) {
            Article saved = articleRepository.save(article);
            int nextVersion = articleVersionRepository.findByArticleIdOrderByVersionNumberDesc(articleId)
                    .stream().mapToInt(ArticleVersion::getVersionNumber).max().orElse(0) + 1;
            ArticleVersion version = new ArticleVersion(nextVersion, saved.getTitle(), saved.getContent(), saved, editor);
            articleVersionRepository.save(version);
        }
        return toResponseDto(article, editor, false);
    }

    public void deleteArticle(Long articleId, String username) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));
        if (!article.getAuthor().getUsername().equals(username)) {
            throw new ForbiddenException("You are not the author of this article");
        }
        articleRepository.delete(article);
    }

    public ArticleResponseDto getArticleBySlug(String slug, String currentUsername) {
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));
        article.incrementViewCount();
        articleRepository.save(article);
        boolean liked = false;
        if (currentUsername != null) {
            User user = userRepository.findByUsername(currentUsername).orElse(null);
            if (user != null) {
                liked = articleLikeRepository.findByArticleAndUser(article, user).isPresent();
                // Record reading history for authenticated users
                recordReadingHistory(user, article);
            }
        }
        return toResponseDto(article, article.getAuthor(), liked);
    }

    public List<ArticleVersionDto> getArticleVersions(Long articleId) {
        List<ArticleVersion> versions = articleVersionRepository.findByArticleIdOrderByVersionNumberDesc(articleId);
        return versions.stream().map(this::toVersionDto).collect(Collectors.toList());
    }

    public Page<ArticleResponseDto> getFeed(List<Long> followedUserIds, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return articleRepository.findFeedByFollowedUsers(followedUserIds, pageable)
                .map(a -> toResponseDto(a, a.getAuthor(), false));
    }

    public Page<ArticleResponseDto> getTrending(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return articleRepository.findTrending(pageable)
                .map(a -> toResponseDto(a, a.getAuthor(), false));
    }

    public Page<ArticleResponseDto> searchArticles(String keyword, Set<String> tags, String author, LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (tags != null && !tags.isEmpty()) {
            return articleRepository.findByTags(tags, pageable).map(a -> toResponseDto(a, a.getAuthor(), false));
        } else if (author != null) {
            return articleRepository.findByAuthor(author, pageable).map(a -> toResponseDto(a, a.getAuthor(), false));
        } else if (startDate != null && endDate != null) {
            return articleRepository.findByDateRange(startDate, endDate, pageable).map(a -> toResponseDto(a, a.getAuthor(), false));
        } else if (keyword != null && !keyword.isEmpty()) {
            return articleRepository.searchByKeyword(keyword.toLowerCase(), pageable).map(a -> toResponseDto(a, a.getAuthor(), false));
        } else {
            return articleRepository.findAllPublished(pageable).map(a -> toResponseDto(a, a.getAuthor(), false));
        }
    }

    public CursorPage<ArticleResponseDto> getArticlesCursorPage(String cursor, int size) {
        List<Article> articles;
        Pageable pageable = PageRequest.of(0, size);
        if (cursor == null || cursor.isEmpty()) {
            articles = articleRepository.findFirstPage(pageable);
        } else {
            String[] parts = cursor.split("_");
            LocalDateTime createdAt = LocalDateTime.parse(parts[0]);
            Long id = Long.parseLong(parts[1]);
            articles = articleRepository.findNextPage(createdAt, id, pageable);
        }
        List<ArticleResponseDto> content = articles.stream()
                .map(a -> toResponseDto(a, a.getAuthor(), false))
                .collect(Collectors.toList());
        String nextCursor = null;
        boolean hasNext = false;
        if (articles.size() == size) {
            Article last = articles.get(articles.size() - 1);
            nextCursor = last.getCreatedAt().toString() + "_" + last.getId();
            hasNext = true;
        }
        return new CursorPage<>(content, nextCursor, hasNext);
    }

    // Helper methods
    private ArticleResponseDto toResponseDto(Article article, User author, boolean likedByCurrentUser) {
        ArticleResponseDto dto = new ArticleResponseDto();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setSlug(article.getSlug());
        dto.setContent(article.getContent());
        dto.setSummary(article.getSummary());
        dto.setCoverImageUrl(article.getCoverImageUrl());
        dto.setReadTimeMinutes(article.getReadTimeMinutes());
        dto.setViewCount(article.getViewCount());
        dto.setLikeCount(article.getLikeCount());
        dto.setCommentCount(article.getCommentCount());
        dto.setPublished(article.isPublished());
        dto.setFeatured(article.isFeatured());
        dto.setCreatedAt(article.getCreatedAt());
        dto.setUpdatedAt(article.getUpdatedAt());
        dto.setPublishedAt(article.getPublishedAt());
        dto.setAuthor(new UserProfileDto(author.getId(), author.getUsername(), author.getEmail(), author.getBio(), author.getProfileImageUrl(), author.getCreatedAt()));
        dto.setTags(article.getTags().stream().map(Tag::getName).collect(Collectors.toSet()));
        dto.setLikedByCurrentUser(likedByCurrentUser);
        return dto;
    }

    private ArticleVersionDto toVersionDto(ArticleVersion version) {
        ArticleVersionDto dto = new ArticleVersionDto();
        dto.setId(version.getId());
        dto.setVersionNumber(version.getVersionNumber());
        dto.setTitle(version.getTitle());
        dto.setContent(version.getContent());
        dto.setSummary(version.getSummary());
        dto.setCoverImageUrl(version.getCoverImageUrl());
        dto.setCreatedAt(version.getCreatedAt());
        dto.setChangeDescription(version.getChangeDescription());
        dto.setCreatedBy(version.getCreatedBy().getUsername());
        return dto;
    }

    private void recordReadingHistory(User user, Article article) {
        try {
            // Check if reading history already exists
            Optional<ReadingHistory> existing = readingHistoryRepository.findByUserAndArticle(user, article);
            if (existing.isPresent()) {
                // Update the read timestamp
                ReadingHistory history = existing.get();
                history.setReadAt(LocalDateTime.now());
                readingHistoryRepository.save(history);
            } else {
                // Create new reading history
                ReadingHistory history = new ReadingHistory(user, article, LocalDateTime.now());
                readingHistoryRepository.save(history);
            }
        } catch (Exception e) {
            // Log error but don't fail the article request
            System.err.println("Failed to record reading history: " + e.getMessage());
        }
    }
} 