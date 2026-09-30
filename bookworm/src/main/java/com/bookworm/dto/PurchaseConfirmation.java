package com.bookworm.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data @Builder
public class PurchaseConfirmation {
    private String orderId;
    private String message;
    private List<OrderItemDto> items;
    private BigDecimal totalAmount;
    private String currency;
}
