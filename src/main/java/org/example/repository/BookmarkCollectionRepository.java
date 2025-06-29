package org.example.repository;

import org.example.entity.BookmarkCollection;
import org.example.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkCollectionRepository extends JpaRepository<BookmarkCollection, Long> {
    List<BookmarkCollection> findByOwner(User owner);
    
    Page<BookmarkCollection> findByOwner(User owner, Pageable pageable);
    
    List<BookmarkCollection> findByOwnerAndIsPublicTrue(User owner);
    
    Page<BookmarkCollection> findByIsPublicTrue(Pageable pageable);
} 