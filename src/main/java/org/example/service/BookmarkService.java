package org.example.service;

import org.example.entity.Bookmark;
import org.example.entity.User;
import org.example.dto.BookmarkCollectionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookmarkService {
    void addBookmark(Long articleId, String username);
    void removeBookmark(Long articleId, String username);
    boolean isBookmarked(Long articleId, String username);
    Page<BookmarkCollectionDto> getUserBookmarks(String username, Pageable pageable);
    List<BookmarkCollectionDto> getBookmarksForArticle(Long articleId, Pageable pageable);
    Long getBookmarkCountForArticle(Long articleId);
}
