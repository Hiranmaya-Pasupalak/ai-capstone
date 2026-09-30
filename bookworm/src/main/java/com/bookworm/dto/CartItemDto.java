package com.bookworm.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data @Builder
public class CartItemDto {
    private BookSummaryDto book;
    private int quantity;
    private BigDecimal itemTotal;
}
