package com.bookworm.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class ReviewDto {
    private String id;
    private String bookId;
    private String reviewerName;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
