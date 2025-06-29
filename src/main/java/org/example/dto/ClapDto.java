package org.example.dto;

public class ClapDto {
    private Long articleId;
    private Integer clapCount;
    private Long totalClaps;
    private boolean hasClapped;
    
    // Constructors
    public ClapDto() {}
    
    public ClapDto(Long articleId, Integer clapCount) {
        this.articleId = articleId;
        this.clapCount = clapCount;
    }
    
    // Getters and Setters
    public Long getArticleId() {
        return articleId;
    }
    
    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }
    
    public Integer getClapCount() {
        return clapCount;
    }
    
    public void setClapCount(Integer clapCount) {
        this.clapCount = clapCount;
    }
    
    public Long getTotalClaps() {
        return totalClaps;
    }
    
    public void setTotalClaps(Long totalClaps) {
        this.totalClaps = totalClaps;
    }
    
    public boolean isHasClapped() {
        return hasClapped;
    }
    
    public void setHasClapped(boolean hasClapped) {
        this.hasClapped = hasClapped;
    }
} 