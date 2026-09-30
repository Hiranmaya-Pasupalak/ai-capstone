package com.bookworm.controller;

import com.bookworm.dto.*;
import com.bookworm.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<PagedResponse<OrderDto>> listOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(orderService.listOrders(userDetails.getUsername(), page, pageSize));
    }

    @PostMapping
    public ResponseEntity<OrderCreatedResponse> placeOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PlaceOrderRequest request) {
        return ResponseEntity.status(201).body(orderService.placeOrder(userDetails.getUsername(), request));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrder(userDetails.getUsername(), orderId));
    }

    @PostMapping("/{orderId}/buy-again")
    public ResponseEntity<CartDto> buyAgain(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String orderId) {
        return ResponseEntity.ok(orderService.buyAgain(userDetails.getUsername(), orderId));
    }
}
