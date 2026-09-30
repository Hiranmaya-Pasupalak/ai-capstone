package com.bookworm.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data @Builder
public class CartDto {
    private List<CartItemDto> items;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal deliveryCharges;
    private String couponCode;
    private BigDecimal couponDiscount;
    private BigDecimal giftPointsDiscount;
    private BigDecimal totalAmount;
    private String currency;
}
