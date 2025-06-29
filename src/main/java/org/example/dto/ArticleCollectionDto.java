package org.example.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class ArticleCollectionDto {
    
    private Long id;
    private String name;
    private String description;
    private boolean isPublic;
    private boolean isCollaborative;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserProfileDto owner;
    private int articleCount;
    private int collaboratorCount;
    private List<ArticleResponseDto> articles;
    private Set<String> tags;
    private List<UserProfileDto> collaborators;
    
    // Constructors
    public ArticleCollectionDto() {}
    
    public ArticleCollectionDto(String name, String description, boolean isPublic, boolean isCollaborative) {
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.isCollaborative = isCollaborative;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isPublic() {
        return isPublic;
    }
    
    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
    
    public boolean isCollaborative() {
        return isCollaborative;
    }
    
    public void setCollaborative(boolean collaborative) {
        isCollaborative = collaborative;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public UserProfileDto getOwner() {
        return owner;
    }
    
    public void setOwner(UserProfileDto owner) {
        this.owner = owner;
    }
    
    public int getArticleCount() {
        return articleCount;
    }
    
    public void setArticleCount(int articleCount) {
        this.articleCount = articleCount;
    }
    
    public int getCollaboratorCount() {
        return collaboratorCount;
    }
    
    public void setCollaboratorCount(int collaboratorCount) {
        this.collaboratorCount = collaboratorCount;
    }
    
    public List<ArticleResponseDto> getArticles() {
        return articles;
    }
    
    public void setArticles(List<ArticleResponseDto> articles) {
        this.articles = articles;
    }
    
    public Set<String> getTags() {
        return tags;
    }
    
    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
    
    public List<UserProfileDto> getCollaborators() {
        return collaborators;
    }
    
    public void setCollaborators(List<UserProfileDto> collaborators) {
        this.collaborators = collaborators;
    }
} 