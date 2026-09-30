package com.bookworm.service;

import com.bookworm.dto.*;

public interface OrderService {
    PagedResponse<OrderDto> listOrders(String userEmail, int page, int pageSize);
    OrderCreatedResponse placeOrder(String userEmail, PlaceOrderRequest request);
    OrderDto getOrder(String userEmail, String orderId);
    CartDto buyAgain(String userEmail, String orderId);
}
