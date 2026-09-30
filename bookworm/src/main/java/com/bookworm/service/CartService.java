package com.bookworm.service;

import com.bookworm.dto.*;

public interface CartService {
    CartDto getCart(String userEmail);
    CartDto addItem(String userEmail, CartItemRequest request);
    CartDto updateItem(String userEmail, String bookId, int quantity);
    CartDto removeItem(String userEmail, String bookId);
    CartDto applyCoupon(String userEmail, String couponCode);
    CartDto removeCoupon(String userEmail);
}
