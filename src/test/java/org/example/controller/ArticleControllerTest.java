package org.example.controller;

import org.example.dto.ArticleCreateDto;
import org.example.dto.ArticleEditDto;
import org.example.dto.ArticleResponseDto;
import org.example.dto.UserProfileDto;
import org.example.service.ArticleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArticleController.class)
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleService articleService;

    private ArticleCreateDto testArticle;

    @BeforeEach
    void setUp() {
        testArticle = new ArticleCreateDto();
        testArticle.setTitle("Test Article");
        testArticle.setContent("This is a test article content.");
        testArticle.setSummary("A test article summary.");
        testArticle.setTags(Set.of("test", "article"));
    }

    @Test
    void testCreateArticle() throws Exception {
        ArticleResponseDto response = new ArticleResponseDto();
        response.setId(1L);
        response.setTitle("Test Article");
        response.setSlug("test-article");
        response.setContent("Test content");
        response.setAuthor(new UserProfileDto(1L, "testuser", "test@example.com", null, null, null));
        
        when(articleService.createArticle(any(ArticleCreateDto.class), any(String.class)))
            .thenReturn(response);

        mockMvc.perform(post("/api/v1/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Test Article\",\"content\":\"Test content\",\"summary\":\"Test summary\",\"tags\":[\"test\"]}")
                .header("Authorization", "Bearer test-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("title").value("Test Article"))
            .andExpect(jsonPath("slug").value("test-article"))
            .andExpect(jsonPath("author.username").value("testuser"));
    }

    @Test
    void testGetArticle() throws Exception {
        ArticleResponseDto response = new ArticleResponseDto();
        response.setId(1L);
        response.setTitle("Test Article");
        response.setSlug("test-article");
        response.setContent("Test content");
        response.setAuthor(new UserProfileDto(1L, "testuser", "test@example.com", null, null, null));
        
        when(articleService.getArticleBySlug(any(String.class), any(String.class)))
            .thenReturn(response);

        mockMvc.perform(get("/api/v1/articles/slug/test-article")
                .header("Authorization", "Bearer test-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("title").value("Test Article"))
            .andExpect(jsonPath("slug").value("test-article"))
            .andExpect(jsonPath("author.username").value("testuser"));
    }

    @Test
    void testEditArticle() throws Exception {
        ArticleEditDto editDto = new ArticleEditDto();
        editDto.setTitle("Updated Title");

        ArticleResponseDto response = new ArticleResponseDto();
        response.setId(1L);
        response.setTitle("Updated Title");
        response.setSlug("test-article");
        response.setContent("Updated content");
        response.setAuthor(new UserProfileDto(1L, "testuser", "test@example.com", null, null, null));
        
        when(articleService.editArticle(any(Long.class), any(ArticleEditDto.class), any(String.class)))
            .thenReturn(response);

        mockMvc.perform(put("/api/v1/articles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Updated Title\",\"content\":\"Updated content\"}")
                .header("Authorization", "Bearer test-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("title").value("Updated Title"))
            .andExpect(jsonPath("content").value("Updated content"))
            .andExpect(jsonPath("author.username").value("testuser"));
    }

    @Test
    void testDeleteArticle() throws Exception {
        mockMvc.perform(delete("/api/v1/articles/1")
                .header("Authorization", "Bearer test-token"))
            .andExpect(status().isNoContent());
    }
}
