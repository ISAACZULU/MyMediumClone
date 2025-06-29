package org.example.dto;

import java.time.LocalDateTime;

public class UserProfileDto {
    
    private Long id;
    private String username;
    private String email;
    private String bio;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private int followersCount;
    private int followingCount;
    private boolean isFollowing;
    
    // Constructors
    public UserProfileDto() {}
    
    public UserProfileDto(Long id, String username, String email, String bio, 
                         String profileImageUrl, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public int getFollowersCount() {
        return followersCount;
    }
    
    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }
    
    public int getFollowingCount() {
        return followingCount;
    }
    
    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }
    
    public boolean isFollowing() {
        return isFollowing;
    }
    
    public void setFollowing(boolean following) {
        isFollowing = following;
    }
} 