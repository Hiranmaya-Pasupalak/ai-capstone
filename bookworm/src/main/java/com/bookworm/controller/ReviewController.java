package com.bookworm.controller;

import com.bookworm.dto.*;
import com.bookworm.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books/{bookId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<PagedResponse<ReviewDto>> listReviews(
            @PathVariable String bookId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(reviewService.listReviews(bookId, page, pageSize));
    }

    @PostMapping
    public ResponseEntity<ReviewDto> createReview(
            @PathVariable String bookId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ReviewRequest request) {
        ReviewDto review = reviewService.createReview(bookId, userDetails.getUsername(), request);
        return ResponseEntity.status(201).body(review);
    }
}
