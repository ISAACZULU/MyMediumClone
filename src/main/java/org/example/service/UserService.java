package org.example.service;

import org.example.dto.AuthResponseDto;
import org.example.dto.PasswordResetDto;
import org.example.dto.UserLoginDto;
import org.example.dto.UserProfileDto;
import org.example.dto.UserRegistrationDto;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.example.exception.PasswordMismatchException;
import org.example.exception.UserAlreadyExistsException;
import org.example.exception.ResourceNotFoundException;
import org.example.exception.ForbiddenException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + username));
        return user;
    }
    
    public AuthResponseDto registerUser(UserRegistrationDto registrationDto) {
        // Validate password confirmation
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords do not match");
        }
        
        // Check if username or email already exists
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new UserAlreadyExistsException("Username is already taken");
        }
        
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new UserAlreadyExistsException("Email is already registered");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setEmailVerified(true); // For simplicity, we'll skip email verification
        
        User savedUser = userRepository.save(user);
        
        // Generate JWT token
        String token = tokenProvider.generateToken(savedUser.getUsername());
        
        // Convert to DTO
        UserProfileDto userProfileDto = convertToUserProfileDto(savedUser);
        
        return new AuthResponseDto(token, userProfileDto);
    }
    
    public AuthResponseDto loginUser(UserLoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(), loginDto.getPassword())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            String token = tokenProvider.generateToken(authentication);
            User user = (User) authentication.getPrincipal();
            UserProfileDto userProfileDto = convertToUserProfileDto(user);
            
            return new AuthResponseDto(token, userProfileDto);
        } catch (Exception e) {
            throw new RuntimeException("Invalid username/email or password");
        }
    }
    
    public UserProfileDto getCurrentUserProfile() {
        User currentUser = getCurrentUser();
        return convertToUserProfileDto(currentUser);
    }
    
    public UserProfileDto updateUserProfile(String bio, String profileImageUrl) {
        User currentUser = getCurrentUser();
        
        if (bio != null) {
            currentUser.setBio(bio);
        }
        
        if (profileImageUrl != null) {
            currentUser.setProfileImageUrl(profileImageUrl);
        }
        
        User updatedUser = userRepository.save(currentUser);
        return convertToUserProfileDto(updatedUser);
    }
    
    public UserProfileDto getUserProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        User currentUser = getCurrentUser();
        UserProfileDto profileDto = convertToUserProfileDto(user);
        profileDto.setFollowing(currentUser.isFollowing(user));
        
        return profileDto;
    }
    
    public void followUser(String username) {
        User currentUser = getCurrentUser();
        User userToFollow = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (currentUser.equals(userToFollow)) {
            throw new ForbiddenException("You cannot follow yourself");
        }
        
        currentUser.follow(userToFollow);
        userRepository.save(currentUser);
    }
    
    public void unfollowUser(String username) {
        User currentUser = getCurrentUser();
        User userToUnfollow = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        currentUser.unfollow(userToUnfollow);
        userRepository.save(currentUser);
    }
    
    public List<UserProfileDto> getFollowers(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return user.getFollowers().stream()
                .map(this::convertToUserProfileDto)
                .collect(Collectors.toList());
    }
    
    public List<UserProfileDto> getFollowing(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return user.getFollowing().stream()
                .map(this::convertToUserProfileDto)
                .collect(Collectors.toList());
    }
    
    public AuthResponseDto requestPasswordReset(PasswordResetDto resetDto) {
        User user = userRepository.findByEmail(resetDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with this email"));
        
        // Generate reset token
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(24));
        
        userRepository.save(user);
        
        // In a real application, you would send an email with the reset token
        // For now, we'll just return a success message
        return new AuthResponseDto("Password reset link sent to your email");
    }
    
    public AuthResponseDto resetPassword(PasswordResetDto resetDto) {
        if (!resetDto.getNewPassword().equals(resetDto.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
        
        User user = userRepository.findByResetToken(resetDto.getResetToken())
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));
        
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }
        
        user.setPassword(passwordEncoder.encode(resetDto.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        
        userRepository.save(user);
        
        return new AuthResponseDto("Password reset successfully");
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }
    
    private UserProfileDto convertToUserProfileDto(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setBio(user.getBio());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setFollowersCount(user.getFollowersCount());
        dto.setFollowingCount(user.getFollowingCount());
        return dto;
    }
} 