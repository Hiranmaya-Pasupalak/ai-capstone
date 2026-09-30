package com.bookworm.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder
public class OrderDto {
    private String id;
    private String status;
    private List<OrderItemDto> items;
    private AddressDto deliveryAddress;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal deliveryCharges;
    private BigDecimal discount;
    private BigDecimal totalAmount;
    private String currency;
    private LocalDateTime createdAt;
    private String paymentMethod;
}
