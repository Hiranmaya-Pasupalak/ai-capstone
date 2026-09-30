package com.bookworm.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data @Builder
public class PagedResponse<T> {
    private List<T> data;
    private PaginationDto pagination;

    @Data @Builder
    public static class PaginationDto {
        private int page;
        private int pageSize;
        private long totalItems;
        private int totalPages;
    }
}
