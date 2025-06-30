package org.example.service.impl;

import org.example.dto.CommentCreateDto;
import org.example.dto.CommentResponseDto;
import org.example.dto.UserProfileDto;
import org.example.entity.Article;
import org.example.entity.Comment;
import org.example.entity.User;
import org.example.repository.ArticleRepository;
import org.example.repository.CommentRepository;
import org.example.repository.UserRepository;
import org.example.service.CommentService;
import org.example.exception.ResourceNotFoundException;
import org.example.exception.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public CommentResponseDto createComment(Long articleId, CommentCreateDto commentDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setAuthor(user);
        comment.setArticle(article);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        // Handle parent comment if specified
        if (commentDto.getParentId() != null) {
            Comment parentComment = commentRepository.findById(commentDto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));
            comment.setParent(parentComment);
        }

        Comment savedComment = commentRepository.save(comment);
        return convertToDto(savedComment);
    }

    @Override
    public CommentResponseDto updateComment(Long commentId, CommentCreateDto commentDto, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if user is the author of the comment
        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new ForbiddenException("Not authorized to update this comment");
        }

        comment.setContent(commentDto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);
        return convertToDto(updatedComment);
    }

    @Override
    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if user is the author of the comment
        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new ForbiddenException("Not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    @Override
    public Page<CommentResponseDto> getArticleComments(Long articleId, Pageable pageable) {
        List<Comment> comments = commentRepository.findFirstPageByArticle(articleId, pageable);
        List<CommentResponseDto> commentDtos = comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new PageImpl<>(commentDtos, pageable, comments.size());
    }

    @Override
    public List<CommentResponseDto> getUserComments(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Comment> comments = commentRepository.findByAuthor(user);
        return comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long getCommentCountForArticle(Long articleId) {
        return commentRepository.countByArticleId(articleId);
    }

    private CommentResponseDto convertToDto(Comment comment) {
        CommentResponseDto dto = new CommentResponseDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        
        // Create UserProfileDto for author
        UserProfileDto authorDto = new UserProfileDto();
        authorDto.setId(comment.getAuthor().getId());
        authorDto.setUsername(comment.getAuthor().getUsername());
        authorDto.setEmail(comment.getAuthor().getEmail());
        authorDto.setBio(comment.getAuthor().getBio());
        authorDto.setProfileImageUrl(comment.getAuthor().getProfileImageUrl());
        dto.setAuthor(authorDto);
        
        if (comment.getParent() != null) {
            dto.setParentId(comment.getParent().getId());
            dto.setReply(true);
        }
        
        return dto;
    }
} 