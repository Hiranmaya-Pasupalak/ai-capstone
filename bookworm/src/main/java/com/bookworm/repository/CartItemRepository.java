package com.bookworm.repository;

import com.bookworm.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, String> {
    Optional<CartItem> findByCartIdAndBookId(String cartId, String bookId);

    @Transactional
    void deleteByCartIdAndBookId(String cartId, String bookId);
}
