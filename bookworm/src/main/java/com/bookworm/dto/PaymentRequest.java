package com.bookworm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentRequest {
    @NotBlank
    private String method; // credit_card, debit_card, upi, wallet

    // Card fields (required when method = credit_card or debit_card)
    private String cardNumber;
    private String nameOnCard;
    private String cvv;
    private String expiryDate; // MM/YYYY

    // UPI
    private String upiId;
}
