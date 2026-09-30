package com.bookworm.controller;

import com.bookworm.dto.*;
import com.bookworm.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{paymentSessionId}/initiate")
    public ResponseEntity<PaymentResponse> initiatePayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String paymentSessionId,
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.initiatePayment(
            userDetails.getUsername(), paymentSessionId, request));
    }

    @PostMapping("/{paymentSessionId}/confirm")
    public ResponseEntity<PurchaseConfirmation> confirmPayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String paymentSessionId,
            @Valid @RequestBody ConfirmPaymentRequest request) {
        return ResponseEntity.ok(paymentService.confirmPayment(
            userDetails.getUsername(), paymentSessionId, request));
    }
}
