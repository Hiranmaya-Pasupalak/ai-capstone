package com.bookworm.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class AuthorDetailDto {
    private String id;
    private String name;
    private String profileImageUrl;
    private String bio;
    private int booksCount;
}
