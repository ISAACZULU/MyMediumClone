package org.example.service.impl;

import org.example.entity.Article;
import org.example.entity.Bookmark;
import org.example.entity.User;
import org.example.dto.BookmarkCollectionDto;
import org.example.repository.ArticleRepository;
import org.example.repository.BookmarkRepository;
import org.example.repository.UserRepository;
import org.example.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.exception.ResourceNotFoundException;
import org.example.exception.ConflictException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookmarkServiceImpl implements BookmarkService {
    
    @Autowired
    private BookmarkRepository bookmarkRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;

    @Override
    @Transactional
    public void addBookmark(Long articleId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));
        
        // Check if bookmark already exists
        if (bookmarkRepository.findByArticleIdAndUser(articleId, user).isPresent()) {
            throw new ConflictException("Article is already bookmarked");
        }
        
        Bookmark bookmark = new Bookmark(article, user);
        bookmarkRepository.save(bookmark);
    }

    @Override
    @Transactional
    public void removeBookmark(Long articleId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Bookmark bookmark = bookmarkRepository.findByArticleIdAndUser(articleId, user)
                .orElseThrow(() -> new RuntimeException("Bookmark not found"));
        
        bookmarkRepository.delete(bookmark);
    }

    @Override
    public boolean isBookmarked(Long articleId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return bookmarkRepository.findByArticleIdAndUser(articleId, user).isPresent();
    }

    @Override
    public Page<BookmarkCollectionDto> getUserBookmarks(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return bookmarkRepository.findByUser(user, pageable)
                .map(this::toCollectionDto);
    }

    @Override
    public List<BookmarkCollectionDto> getBookmarksForArticle(Long articleId, Pageable pageable) {
        return bookmarkRepository.findByArticleId(articleId, pageable)
                .stream()
                .map(this::toCollectionDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long getBookmarkCountForArticle(Long articleId) {
        return bookmarkRepository.getBookmarkCountForArticle(articleId);
    }

    private BookmarkCollectionDto toCollectionDto(Bookmark bookmark) {
        BookmarkCollectionDto dto = new BookmarkCollectionDto();
        dto.setBookmarkId(bookmark.getId());
        dto.setArticleId(bookmark.getArticle().getId());
        dto.setUserId(bookmark.getUser().getId());
        dto.setCreatedAt(bookmark.getCreatedAt());
        return dto;
    }
}
