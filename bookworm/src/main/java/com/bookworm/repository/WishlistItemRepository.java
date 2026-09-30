package com.bookworm.repository;

import com.bookworm.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, String> {
    List<WishlistItem> findByUserId(String userId);
    Optional<WishlistItem> findByUserIdAndBookId(String userId, String bookId);

    @Transactional
    void deleteByUserIdAndBookId(String userId, String bookId);

    boolean existsByUserIdAndBookId(String userId, String bookId);
}
