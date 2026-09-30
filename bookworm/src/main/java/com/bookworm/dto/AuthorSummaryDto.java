package com.bookworm.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class AuthorSummaryDto {
    private String id;
    private String name;
    private String profileImageUrl;
}
