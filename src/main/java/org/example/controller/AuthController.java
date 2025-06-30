package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.AuthResponseDto;
import org.example.dto.PasswordResetDto;
import org.example.dto.UserLoginDto;
import org.example.dto.UserRegistrationDto;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        try {
            AuthResponseDto response = userService.registerUser(registrationDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponseDto(e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginUser(@Valid @RequestBody UserLoginDto loginDto) {
        try {
            AuthResponseDto response = userService.loginUser(loginDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponseDto(e.getMessage()));
        }
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<AuthResponseDto> requestPasswordReset(@RequestBody PasswordResetDto resetDto) {
        try {
            AuthResponseDto response = userService.requestPasswordReset(resetDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponseDto(e.getMessage()));
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<AuthResponseDto> resetPassword(@RequestBody PasswordResetDto resetDto) {
        try {
            AuthResponseDto response = userService.resetPassword(resetDto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new AuthResponseDto(e.getMessage()));
        }
    }
} 