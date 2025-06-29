package org.example.dto;

import jakarta.validation.constraints.Size;
import java.util.Set;

public class ArticleEditDto {
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    private String content;

    @Size(max = 500, message = "Summary cannot exceed 500 characters")
    private String summary;

    private String coverImageUrl;

    private Set<String> tags;

    private boolean published;

    // Constructors
    public ArticleEditDto() {}

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
} 