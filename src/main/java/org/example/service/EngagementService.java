package org.example.service;

import org.example.dto.*;
import org.example.entity.*;
import org.example.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.exception.ResourceNotFoundException;
import org.example.exception.ForbiddenException;
import org.example.exception.NotAllowedException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EngagementService {
    
    private final ClapRepository clapRepository;
    
    private final CommentRepository commentRepository;
    
    private final BookmarkRepository bookmarkRepository;
    
    private final BookmarkCollectionRepository bookmarkCollectionRepository;
    
    private final ArticleRepository articleRepository;
    
    private final UserRepository userRepository;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
    
    // Clap functionality
    @Transactional
    public ClapDto clapArticle(Long articleId, Integer clapCount, String username) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Optional<Clap> existingClap = clapRepository.findByArticleAndUser(article, user);
        Clap clap;
        
        if (existingClap.isPresent()) {
            clap = existingClap.get();
            clap.setClaps(clapCount);
        } else {
            clap = new Clap(article, user, clapCount);
        }
        
        clapRepository.save(clap);
        
        // Update article clap count
        Long totalClaps = clapRepository.getTotalClapsForArticle(article);
        article.setLikeCount(totalClaps);
        articleRepository.save(article);
        
        ClapDto response = new ClapDto();
        response.setArticleId(articleId);
        response.setClapCount(clap.getClapCount());
        response.setTotalClaps(totalClaps);
        response.setHasClapped(true);
        
        return response;
    }
    
    public ClapDto getClapInfo(Long articleId, String username) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));
        
        Long totalClaps = clapRepository.getTotalClapsForArticle(article);
        boolean hasClapped = false;
        Integer userClapCount = 0;
        
        if (username != null) {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                Optional<Clap> userClap = clapRepository.findByArticleAndUser(article, user);
                if (userClap.isPresent()) {
                    hasClapped = true;
                    userClapCount = userClap.get().getClapCount();
                }
            }
        }
        
        ClapDto response = new ClapDto();
        response.setArticleId(articleId);
        response.setClapCount(userClapCount);
        response.setTotalClaps(totalClaps);
        response.setHasClapped(hasClapped);
        
        return response;
    }
    
    // Comment functionality
    @Transactional
    public CommentResponseDto createComment(Long articleId, CommentCreateDto dto, String username) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Comment comment = new Comment(dto.getContent(), article, user);
        
        if (dto.getParentId() != null) {
            Comment parent = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));
            comment.setParent(parent);
        }
        
        Comment saved = commentRepository.save(comment);
        article.incrementCommentCount();
        articleRepository.save(article);
        
        return toCommentResponseDto(saved);
    }
    
    @Transactional
    public CommentResponseDto updateComment(Long commentId, String content, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        
        if (!comment.getAuthor().getUsername().equals(username)) {
            throw new ForbiddenException("You can only edit your own comments");
        }
        
        comment.setContent(content);
        Comment saved = commentRepository.save(comment);
        
        return toCommentResponseDto(saved);
    }
    
    @Transactional
    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        
        if (!comment.getAuthor().getUsername().equals(username)) {
            throw new ForbiddenException("You can only delete your own comments");
        }
        
        Article article = comment.getArticle();
        article.decrementCommentCount();
        articleRepository.save(article);
        
        commentRepository.delete(comment);
    }
    
    @Transactional
    public void flagComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        
        comment.incrementFlagCount();
        if (comment.getFlagCount() >= 3) {
            comment.setFlagged(true);
        }
        
        commentRepository.save(comment);
    }
    
    public List<CommentResponseDto> getArticleComments(Long articleId) {
        List<Comment> comments = commentRepository.findAll().stream()
                .filter(c -> c.getArticle().getId().equals(articleId) && c.getParent() == null && !c.isHidden())
                .collect(Collectors.toList());
        
        return comments.stream()
                .map(this::toCommentResponseDtoWithReplies)
                .collect(Collectors.toList());
    }
    
    public CursorPage<CommentResponseDto> getArticleCommentsCursorPage(Long articleId, String cursor, int size) {
        Pageable pageable = PageRequest.of(0, size);
        List<Comment> comments;
        if (cursor == null || cursor.isEmpty()) {
            comments = commentRepository.findFirstPageByArticle(articleId, pageable);
        } else {
            String[] parts = cursor.split("_");
            LocalDateTime createdAt = LocalDateTime.parse(parts[0]);
            Long id = Long.parseLong(parts[1]);
            comments = commentRepository.findNextPageByArticle(articleId, createdAt, id, pageable);
        }
        List<CommentResponseDto> content = comments.stream()
                .map(this::toCommentResponseDtoWithReplies)
                .collect(Collectors.toList());
        String nextCursor = null;
        boolean hasNext = false;
        if (comments.size() == size) {
            Comment last = comments.get(comments.size() - 1);
            nextCursor = last.getCreatedAt().toString() + "_" + last.getId();
            hasNext = true;
        }
        return new CursorPage<>(content, nextCursor, hasNext);
    }
    
    // Bookmark functionality
    @Transactional
    public void bookmarkArticle(Long articleId, Long collectionId, String username) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        BookmarkCollection collection = null;
        if (collectionId != null) {
            collection = bookmarkCollectionRepository.findById(collectionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));
            if (!collection.getOwner().equals(user)) {
                throw new ForbiddenException("You can only add to your own collections");
            }
        }
        
        Optional<Bookmark> existing = bookmarkRepository.findByArticleAndUser(article, user);
        if (existing.isPresent()) {
            Bookmark bookmark = existing.get();
            bookmark.setCollection(collection);
            bookmarkRepository.save(bookmark);
        } else {
            Bookmark bookmark = new Bookmark(article, user, collection);
            bookmarkRepository.save(bookmark);
        }
    }
    
    @Transactional
    public void removeBookmark(Long articleId, String username) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Optional<Bookmark> bookmark = bookmarkRepository.findByArticleAndUser(article, user);
        if (bookmark.isPresent()) {
            bookmarkRepository.delete(bookmark.get());
        }
    }
    
    @Transactional
    public BookmarkCollectionDto createCollection(BookmarkCollectionDto dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        BookmarkCollection collection = new BookmarkCollection(
                dto.getName(), dto.getDescription(), dto.isPublic(), user);
        
        BookmarkCollection saved = bookmarkCollectionRepository.save(collection);
        
        return toBookmarkCollectionDto(saved);
    }
    
    public List<BookmarkCollectionDto> getUserCollections(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        List<BookmarkCollection> collections = bookmarkCollectionRepository.findByOwner(user);
        
        return collections.stream()
                .map(this::toBookmarkCollectionDto)
                .collect(Collectors.toList());
    }
    
    public BookmarkCollectionDto getCollection(Long collectionId, String username) {
        BookmarkCollection collection = bookmarkCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));
        
        if (!collection.isPublic() && !collection.getOwner().getUsername().equals(username)) {
            throw new NotAllowedException("This collection is private");
        }
        
        return toBookmarkCollectionDtoWithBookmarks(collection);
    }
    
    // Sharing functionality
    public ShareDto generateShareInfo(String slug) {
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        
        ShareDto shareDto = new ShareDto();
        shareDto.setShareUrl(baseUrl + "/articles/" + slug);
        shareDto.setTitle(article.getTitle());
        shareDto.setDescription(article.getSummary() != null ? article.getSummary() : 
                article.getContent().substring(0, Math.min(160, article.getContent().length())));
        shareDto.setImageUrl(article.getCoverImageUrl());
        shareDto.setAuthor(article.getAuthor().getUsername());
        shareDto.setPublishedDate(article.getPublishedAt() != null ? 
                article.getPublishedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : "");
        shareDto.setReadTime(article.getReadTimeMinutes() + " min read");
        
        return shareDto;
    }
    
    // Helper methods
    private CommentResponseDto toCommentResponseDto(Comment comment) {
        CommentResponseDto dto = new CommentResponseDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setLikeCount(comment.getLikeCount());
        dto.setFlagCount(comment.getFlagCount());
        dto.setFlagged(comment.isFlagged());
        dto.setHidden(comment.isHidden());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        dto.setAuthor(new UserProfileDto(
                comment.getAuthor().getId(),
                comment.getAuthor().getUsername(),
                comment.getAuthor().getEmail(),
                comment.getAuthor().getBio(),
                comment.getAuthor().getProfileImageUrl(),
                comment.getAuthor().getCreatedAt()
        ));
        dto.setParentId(comment.getParent() != null ? comment.getParent().getId() : null);
        dto.setReply(comment.isReply());
        dto.setHasReplies(comment.hasReplies());
        
        return dto;
    }
    
    private CommentResponseDto toCommentResponseDtoWithReplies(Comment comment) {
        CommentResponseDto dto = toCommentResponseDto(comment);
        
        List<CommentResponseDto> replies = comment.getReplies().stream()
                .filter(reply -> !reply.isHidden())
                .map(this::toCommentResponseDto)
                .collect(Collectors.toList());
        
        dto.setReplies(replies);
        return dto;
    }
    
    private BookmarkCollectionDto toBookmarkCollectionDto(BookmarkCollection collection) {
        BookmarkCollectionDto dto = new BookmarkCollectionDto();
        dto.setId(collection.getId());
        dto.setName(collection.getName());
        dto.setDescription(collection.getDescription());
        dto.setPublic(collection.isPublic());
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
        dto.setBookmarkCount(collection.getBookmarkCount());
        
        return dto;
    }
    
    private BookmarkCollectionDto toBookmarkCollectionDtoWithBookmarks(BookmarkCollection collection) {
        BookmarkCollectionDto dto = toBookmarkCollectionDto(collection);
        
        List<ArticleResponseDto> bookmarks = collection.getBookmarks().stream()
                .map(bookmark -> {
                    ArticleResponseDto articleDto = new ArticleResponseDto();
                    articleDto.setId(bookmark.getArticle().getId());
                    articleDto.setTitle(bookmark.getArticle().getTitle());
                    articleDto.setSlug(bookmark.getArticle().getSlug());
                    articleDto.setSummary(bookmark.getArticle().getSummary());
                    articleDto.setCoverImageUrl(bookmark.getArticle().getCoverImageUrl());
                    articleDto.setReadTimeMinutes(bookmark.getArticle().getReadTimeMinutes());
                    articleDto.setCreatedAt(bookmark.getArticle().getCreatedAt());
                    articleDto.setAuthor(new UserProfileDto(
                            bookmark.getArticle().getAuthor().getId(),
                            bookmark.getArticle().getAuthor().getUsername(),
                            bookmark.getArticle().getAuthor().getEmail(),
                            bookmark.getArticle().getAuthor().getBio(),
                            bookmark.getArticle().getAuthor().getProfileImageUrl(),
                            bookmark.getArticle().getAuthor().getCreatedAt()
                    ));
                    return articleDto;
                })
                .collect(Collectors.toList());
        
        dto.setBookmarks(bookmarks);
        return dto;
    }
} 