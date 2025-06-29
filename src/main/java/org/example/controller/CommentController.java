package org.example.controller;

import org.example.dto.CommentCreateDto;
import org.example.dto.CommentResponseDto;
import org.example.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@CrossOrigin(origins = "*")
public class CommentController {
    
    @Autowired
    private CommentService commentService;

    @PostMapping("/article/{articleId}")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long articleId,
            @RequestBody CommentCreateDto dto,
            Principal principal) {
        String username = principal.getName();
        CommentResponseDto response = commentService.createComment(articleId, dto, username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/article/{articleId}")
    public ResponseEntity<Page<CommentResponseDto>> getCommentsByArticle(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CommentResponseDto> comments = commentService.getArticleComments(articleId, PageRequest.of(page, size));
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentCreateDto dto,
            Principal principal) {
        String username = principal.getName();
        CommentResponseDto response = commentService.updateComment(commentId, dto, username);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            Principal principal) {
        String username = principal.getName();
        commentService.deleteComment(commentId, username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<CommentResponseDto>> getUserComments(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<CommentResponseDto> comments = commentService.getUserComments(username, PageRequest.of(page, size));
        return ResponseEntity.ok(comments);
    }
}
