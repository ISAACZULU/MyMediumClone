package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "article_collections")
public class ArticleCollection {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Collection name is required")
    @Size(max = 100, message = "Collection name cannot exceed 100 characters")
    @Column(nullable = false)
    private String name;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Column(length = 500)
    private String description;
    
    @Column(name = "is_public")
    private boolean isPublic = false;
    
    @Column(name = "is_collaborative")
    private boolean isCollaborative = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "collection_articles",
        joinColumns = @JoinColumn(name = "collection_id"),
        inverseJoinColumns = @JoinColumn(name = "article_id")
    )
    private Set<Article> articles = new HashSet<>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "collection_collaborators",
        joinColumns = @JoinColumn(name = "collection_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> collaborators = new HashSet<>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "collection_tags",
        joinColumns = @JoinColumn(name = "collection_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public ArticleCollection() {}
    
    public ArticleCollection(String name, User owner) {
        this.name = name;
        this.owner = owner;
    }
    
    public ArticleCollection(String name, String description, boolean isPublic, boolean isCollaborative, User owner) {
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.isCollaborative = isCollaborative;
        this.owner = owner;
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
    
    public User getOwner() {
        return owner;
    }
    
    public void setOwner(User owner) {
        this.owner = owner;
    }
    
    public void setUser(User user) {
        this.owner = user;
    }
    
    public Set<Article> getArticles() {
        return articles;
    }
    
    public void setArticles(Set<Article> articles) {
        this.articles = articles;
    }
    
    public Set<User> getCollaborators() {
        return collaborators;
    }
    
    public void setCollaborators(Set<User> collaborators) {
        this.collaborators = collaborators;
    }
    
    public Set<Tag> getTags() {
        return tags;
    }
    
    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
    
    // Helper methods
    public void addArticle(Article article) {
        this.articles.add(article);
    }
    
    public void removeArticle(Article article) {
        this.articles.remove(article);
    }
    
    public void addCollaborator(User user) {
        this.collaborators.add(user);
    }
    
    public void removeCollaborator(User user) {
        this.collaborators.remove(user);
    }
    
    public void addTag(Tag tag) {
        this.tags.add(tag);
    }
    
    public void removeTag(Tag tag) {
        this.tags.remove(tag);
    }
    
    public int getArticleCount() {
        return articles.size();
    }
    
    public int getCollaboratorCount() {
        return collaborators.size();
    }
    
    public boolean isCollaborator(User user) {
        return collaborators.contains(user);
    }
    
    public boolean canEdit(User user) {
        return owner.equals(user) || (isCollaborative && collaborators.contains(user));
    }
} 