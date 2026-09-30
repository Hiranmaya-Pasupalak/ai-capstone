package com.bookworm.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data @Builder
public class CategoryDto {
    private String id;
    private String name;
    private String slug;
}
