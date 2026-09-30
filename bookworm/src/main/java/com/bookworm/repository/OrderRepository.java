package com.bookworm.repository;

import com.bookworm.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
    Page<Order> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    Optional<Order> findByPaymentSessionId(String paymentSessionId);
}
