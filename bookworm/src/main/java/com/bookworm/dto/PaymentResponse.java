package com.bookworm.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder
public class PaymentResponse {
    private String transactionId;
    private String status; // initiated, success, failed, pending
    private BigDecimal payableAmount;
    private String currency;
    private String paymentMethod;
    private LocalDateTime timestamp;
}
