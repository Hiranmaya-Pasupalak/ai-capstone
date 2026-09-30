package com.bookworm.service;

import com.bookworm.dto.*;

public interface ReviewService {
    PagedResponse<ReviewDto> listReviews(String bookId, int page, int pageSize);
    ReviewDto createReview(String bookId, String userEmail, ReviewRequest request);
}
