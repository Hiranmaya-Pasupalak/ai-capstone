package com.bookworm.service;

import com.bookworm.dto.*;

public interface PaymentService {
    PaymentResponse initiatePayment(String userEmail, String paymentSessionId, PaymentRequest request);
    PurchaseConfirmation confirmPayment(String userEmail, String paymentSessionId, ConfirmPaymentRequest request);
}
