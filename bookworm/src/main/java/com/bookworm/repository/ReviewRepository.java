package com.bookworm.repository;

import com.bookworm.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, String> {
    Page<Review> findByBookId(String bookId, Pageable pageable);
    boolean existsByBookIdAndUserId(String bookId, String userId);
    long countByBookId(String bookId);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.book.id = :bookId")
    double averageRatingByBookId(@Param("bookId") String bookId);
}
