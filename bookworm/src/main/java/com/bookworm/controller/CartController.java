package com.bookworm.controller;

import com.bookworm.dto.*;
import com.bookworm.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartDto> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.getCart(userDetails.getUsername()));
    }

    @PostMapping("/items")
    public ResponseEntity<CartDto> addItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.status(201).body(cartService.addItem(userDetails.getUsername(), request));
    }

    @PatchMapping("/items/{bookId}")
    public ResponseEntity<CartDto> updateItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String bookId,
            @Valid @RequestBody UpdateQuantityRequest request) {
        return ResponseEntity.ok(cartService.updateItem(userDetails.getUsername(), bookId, request.getQuantity()));
    }

    @DeleteMapping("/items/{bookId}")
    public ResponseEntity<CartDto> removeItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String bookId) {
        return ResponseEntity.ok(cartService.removeItem(userDetails.getUsername(), bookId));
    }

    @PostMapping("/coupon")
    public ResponseEntity<CartDto> applyCoupon(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CouponRequest request) {
        return ResponseEntity.ok(cartService.applyCoupon(userDetails.getUsername(), request.getCouponCode()));
    }

    @DeleteMapping("/coupon")
    public ResponseEntity<CartDto> removeCoupon(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.removeCoupon(userDetails.getUsername()));
    }
}
