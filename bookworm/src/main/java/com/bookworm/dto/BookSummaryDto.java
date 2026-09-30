package com.bookworm.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data @Builder
public class BookSummaryDto {
    private String id;
    private String title;
    private AuthorSummaryDto author;
    private String coverImageUrl;
    private String format;
    private BigDecimal price;
    private String currency;
    private List<CategoryDto> categories;
    private BigDecimal rating;
    private LocalDate estimatedDeliveryDate;
}
