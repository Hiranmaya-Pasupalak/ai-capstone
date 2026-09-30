package com.bookworm.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data @Builder
public class OrderCreatedResponse {
    private OrderDto order;
    private String paymentSessionId;
    private BigDecimal payableAmount;
}
