package com.bookworm.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data @Builder
public class OrderItemDto {
    private BookSummaryDto book;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal itemTotal;
    private LocalDate estimatedDeliveryDate;
}
