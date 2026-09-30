package com.bookworm.service.impl;

import com.bookworm.dto.*;
import com.bookworm.entity.Order;
import com.bookworm.exception.*;
import com.bookworm.mapper.EntityMapper;
import com.bookworm.repository.*;
import com.bookworm.service.CartService;
import com.bookworm.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final EntityMapper mapper;
    private final CartService cartService;

    @Override
    @Transactional
    public PaymentResponse initiatePayment(String userEmail, String paymentSessionId, PaymentRequest request) {
        Order order = orderRepository.findByPaymentSessionId(paymentSessionId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment session not found: " + paymentSessionId));

        // Validate method
        Order.PaymentMethod method;
        try {
            method = Order.PaymentMethod.valueOf(request.getMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid payment method: " + request.getMethod());
        }

        order.setPaymentMethod(method);
        orderRepository.save(order);

        // Simulate payment gateway — in production wire a real gateway here
        String transactionId = "TXN-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return PaymentResponse.builder()
            .transactionId(transactionId)
            .status("success")
            .payableAmount(order.getTotalAmount())
            .currency(order.getCurrency())
            .paymentMethod(request.getMethod())
            .timestamp(LocalDateTime.now())
            .build();
    }

    @Override
    @Transactional
    public PurchaseConfirmation confirmPayment(String userEmail, String paymentSessionId, ConfirmPaymentRequest request) {
        Order order = orderRepository.findByPaymentSessionId(paymentSessionId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment session not found: " + paymentSessionId));

        order.setStatus(Order.Status.CONFIRMED);
        order.setTransactionId(request.getTransactionId());
        orderRepository.save(order);

        // Clear the user's cart
        var user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));
        cartRepository.findByUserId(user.getId()).ifPresent(cart -> {
            cart.getItems().clear();
            cart.setCouponCode(null);
            cart.setCouponDiscount(java.math.BigDecimal.ZERO);
            cart.setGiftPointsDiscount(java.math.BigDecimal.ZERO);
            cartRepository.save(cart);
        });

        return PurchaseConfirmation.builder()
            .orderId(order.getId())
            .message("Your purchase of the following reads is successful")
            .items(order.getItems().stream().map(mapper::toDto).collect(Collectors.toList()))
            .totalAmount(order.getTotalAmount())
            .currency(order.getCurrency())
            .build();
    }
}
