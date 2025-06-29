package org.example.controller;

import org.example.dto.UserProfileDto;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getCurrentUserProfile() {
        try {
            UserProfileDto profile = userService.getCurrentUserProfile();
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/profile")
    public ResponseEntity<UserProfileDto> updateUserProfile(
            @RequestParam(required = false) String bio,
            @RequestParam(required = false) String profileImageUrl) {
        try {
            UserProfileDto updatedProfile = userService.updateUserProfile(bio, profileImageUrl);
            return ResponseEntity.ok(updatedProfile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/public/{username}")
    public ResponseEntity<UserProfileDto> getUserProfileByUsername(@PathVariable String username) {
        try {
            UserProfileDto profile = userService.getUserProfileByUsername(username);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/follow/{username}")
    public ResponseEntity<String> followUser(@PathVariable String username) {
        try {
            userService.followUser(username);
            return ResponseEntity.ok("Successfully followed " + username);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/follow/{username}")
    public ResponseEntity<String> unfollowUser(@PathVariable String username) {
        try {
            userService.unfollowUser(username);
            return ResponseEntity.ok("Successfully unfollowed " + username);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/{username}/followers")
    public ResponseEntity<List<UserProfileDto>> getFollowers(@PathVariable String username) {
        try {
            List<UserProfileDto> followers = userService.getFollowers(username);
            return ResponseEntity.ok(followers);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{username}/following")
    public ResponseEntity<List<UserProfileDto>> getFollowing(@PathVariable String username) {
        try {
            List<UserProfileDto> following = userService.getFollowing(username);
            return ResponseEntity.ok(following);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 