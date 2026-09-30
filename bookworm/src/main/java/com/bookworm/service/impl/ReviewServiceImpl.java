package com.bookworm.service.impl;

import com.bookworm.dto.*;
import com.bookworm.entity.*;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.mapper.EntityMapper;
import com.bookworm.repository.*;
import com.bookworm.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final EntityMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReviewDto> listReviews(String bookId, int page, int pageSize) {
        bookRepository.findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));

        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
        Page<Review> reviewPage = reviewRepository.findByBookId(bookId, pageable);

        List<ReviewDto> data = reviewPage.getContent().stream()
            .map(mapper::toDto).collect(Collectors.toList());

        return PagedResponse.<ReviewDto>builder()
            .data(data)
            .pagination(PagedResponse.PaginationDto.builder()
                .page(page).pageSize(pageSize)
                .totalItems(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .build())
            .build();
    }

    @Override
    @Transactional
    public ReviewDto createReview(String bookId, String userEmail, ReviewRequest request) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        Review review = Review.builder()
            .book(book)
            .user(user)
            .rating(request.getRating())
            .comment(request.getComment())
            .build();
        review = reviewRepository.save(review);

        // Update book's average rating and review count using aggregate queries
        long count = reviewRepository.countByBookId(bookId);
        double avg  = reviewRepository.averageRatingByBookId(bookId);
        book.setReviewCount((int) count);
        book.setAverageRating(java.math.BigDecimal.valueOf(avg).setScale(2, java.math.RoundingMode.HALF_UP));
        bookRepository.save(book);

        return mapper.toDto(review);
    }
}
