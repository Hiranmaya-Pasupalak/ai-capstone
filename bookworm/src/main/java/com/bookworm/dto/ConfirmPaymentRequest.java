package com.bookworm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfirmPaymentRequest {
    @NotBlank
    private String transactionId;
}
