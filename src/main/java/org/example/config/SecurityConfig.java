package org.example.config;

import org.example.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    @Lazy
    private UserDetailsService userDetailsService;
    
    @Value("${app.rate-limit.default-limit:100}")
    private int defaultLimit;
    @Value("${app.rate-limit.default-window:3600}")
    private int defaultWindow;
    @Value("${app.rate-limit.auth-limit:10}")
    private int authLimit;
    @Value("${app.rate-limit.auth-window:3600}")
    private int authWindow;

    private static class RateLimitInfo {
        AtomicInteger count = new AtomicInteger(0);
        AtomicLong windowStart = new AtomicLong(System.currentTimeMillis());
    }
    private final Map<String, RateLimitInfo> rateLimits = new ConcurrentHashMap<>();
    
    
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> 
                auth.requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/h2-console/**").permitAll()
                    .requestMatchers("/api/users/public/**").permitAll()
                    .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        // For H2 console
        http.headers(headers -> headers.frameOptions().disable());
        
        return http.build();
    }

    @Bean
    @Order(1)
    public Filter rateLimitingFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
                String clientIp = request.getRemoteAddr();
                boolean isAuth = request.getRequestURI().startsWith("/api/auth");
                int limit = isAuth ? authLimit : defaultLimit;
                int window = isAuth ? authWindow : defaultWindow;
                long now = System.currentTimeMillis();
                RateLimitInfo info = rateLimits.computeIfAbsent(clientIp, k -> new RateLimitInfo());
                synchronized (info) {
                    if (now - info.windowStart.get() > window * 1000L) {
                        info.count.set(0);
                        info.windowStart.set(now);
                    }
                    if (info.count.incrementAndGet() > limit) {
                        response.setStatus(429);
                        response.getWriter().write("Too many requests. Please try again later.");
                        return;
                    }
                }
                chain.doFilter(request, response);
            }
        };
    }
} 