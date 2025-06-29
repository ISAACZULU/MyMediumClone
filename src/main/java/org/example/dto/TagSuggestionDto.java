package org.example.dto;

public class TagSuggestionDto {
    
    private Long id;
    private String name;
    private String description;
    private Long articleCount;
    private Double trendingScore;
    private boolean isTrending;
    
    // Constructors
    public TagSuggestionDto() {}
    
    public TagSuggestionDto(String name) {
        this.name = name;
    }
    
    public TagSuggestionDto(Long id, String name, Long articleCount) {
        this.id = id;
        this.name = name;
        this.articleCount = articleCount;
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
    
    public Long getArticleCount() {
        return articleCount;
    }
    
    public void setArticleCount(Long articleCount) {
        this.articleCount = articleCount;
    }
    
    public Double getTrendingScore() {
        return trendingScore;
    }
    
    public void setTrendingScore(Double trendingScore) {
        this.trendingScore = trendingScore;
    }
    
    public boolean isTrending() {
        return isTrending;
    }
    
    public void setTrending(boolean trending) {
        isTrending = trending;
    }
} 