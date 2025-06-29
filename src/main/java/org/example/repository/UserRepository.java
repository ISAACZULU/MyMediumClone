package org.example.repository;

import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    Optional<User> findByResetToken(String resetToken);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.username LIKE %:query% OR u.bio LIKE %:query%")
    List<User> searchUsers(@Param("query") String query);
    
    @Query("SELECT u FROM User u JOIN u.followers f WHERE f.id = :userId")
    List<User> findUsersFollowingUser(@Param("userId") Long userId);
    
    @Query("SELECT u FROM User u JOIN u.following f WHERE f.id = :userId")
    List<User> findUsersFollowedByUser(@Param("userId") Long userId);

    List<User> findByIdIn(List<Long> ids);
    
    @Query("SELECT u FROM User u WHERE u.lastLogin >= :dateTime")
    List<User> findByLastLoginAfter(@Param("dateTime") java.time.LocalDateTime dateTime);
} 