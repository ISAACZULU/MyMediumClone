package org.example.service;

import org.example.dto.CommentCreateDto;
import org.example.dto.CommentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CommentService {
    
    CommentResponseDto createComment(Long articleId, CommentCreateDto commentDto, String username);
    
    CommentResponseDto updateComment(Long commentId, CommentCreateDto commentDto, String username);
    
    void deleteComment(Long commentId, String username);
    
    Page<CommentResponseDto> getArticleComments(Long articleId, Pageable pageable);
    
    List<CommentResponseDto> getUserComments(String username, Pageable pageable);
    
    Long getCommentCountForArticle(Long articleId);
} 