package com.bookworm.service.impl;

import com.bookworm.dto.*;
import com.bookworm.entity.*;
import com.bookworm.exception.*;
import com.bookworm.mapper.EntityMapper;
import com.bookworm.repository.*;
import com.bookworm.service.CartService;
import com.bookworm.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.12");

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final EntityMapper mapper;
    private final CartService cartService;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderDto> listOrders(String userEmail, int page, int pageSize) {
        User user = getUser(userEmail);
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Order> orderPage = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);

        List<OrderDto> data = orderPage.getContent().stream()
            .map(mapper::toDto).collect(Collectors.toList());

        return PagedResponse.<OrderDto>builder()
            .data(data)
            .pagination(PagedResponse.PaginationDto.builder()
                .page(page).pageSize(pageSize)
                .totalItems(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .build())
            .build();
    }

    @Override
    @Transactional
    public OrderCreatedResponse placeOrder(String userEmail, PlaceOrderRequest request) {
        User user = getUser(userEmail);
        Cart cart = cartRepository.findByUserId(user.getId())
            .orElseThrow(() -> new BadRequestException("Cart is empty"));
        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        Address deliveryAddress = resolveAddress(user, request.getDeliveryAddress());

        // Build order totals
        BigDecimal subtotal = cart.getItems().stream()
            .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tax = subtotal.multiply(TAX_RATE).setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal couponDiscount = cart.getCouponDiscount() != null ? cart.getCouponDiscount() : BigDecimal.ZERO;

        BigDecimal giftDiscount = BigDecimal.ZERO;
        if (request.isRedeemGiftPoints() && user.getGiftPointsBalance() > 0) {
            giftDiscount = BigDecimal.valueOf(user.getGiftPointsBalance());
            user.setGiftPointsBalance(0);
            userRepository.save(user);
        }

        BigDecimal total = subtotal.add(tax).subtract(couponDiscount).subtract(giftDiscount)
            .max(BigDecimal.ZERO).setScale(2, java.math.RoundingMode.HALF_UP);

        String sessionId = UUID.randomUUID().toString();

        Order order = Order.builder()
            .user(user)
            .deliveryAddress(deliveryAddress)
            .subtotal(subtotal)
            .tax(tax)
            .deliveryCharges(BigDecimal.ZERO)
            .discount(couponDiscount.add(giftDiscount))
            .totalAmount(total)
            .currency("INR")
            .status(Order.Status.PENDING_PAYMENT)
            .paymentSessionId(sessionId)
            .build();

        // Map cart items → order items
        List<OrderItem> orderItems = cart.getItems().stream().map(ci -> OrderItem.builder()
            .order(order)
            .book(ci.getBook())
            .quantity(ci.getQuantity())
            .unitPrice(ci.getUnitPrice())
            .itemTotal(ci.getUnitPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
            .estimatedDeliveryDate(LocalDate.now().plusDays(5))
            .build()
        ).collect(Collectors.toList());
        order.setItems(orderItems);

        Order saved = orderRepository.save(order);
        return OrderCreatedResponse.builder()
            .order(mapper.toDto(saved))
            .paymentSessionId(sessionId)
            .payableAmount(total)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrder(String userEmail, String orderId) {
        User user = getUser(userEmail);
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Order not found: " + orderId);
        }
        return mapper.toDto(order);
    }

    @Override
    @Transactional
    public CartDto buyAgain(String userEmail, String orderId) {
        User user = getUser(userEmail);
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Order not found: " + orderId);
        }

        for (OrderItem oi : order.getItems()) {
            CartItemRequest req = new CartItemRequest();
            req.setBookId(oi.getBook().getId());
            req.setQuantity(oi.getQuantity());
            cartService.addItem(userEmail, req);
        }
        return cartService.getCart(userEmail);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private User getUser(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private Address resolveAddress(User user, PlaceOrderRequest.DeliveryAddressInput input) {
        if (input.getAddressId() != null && !input.getAddressId().isBlank()) {
            return addressRepository.findByIdAndUserId(input.getAddressId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found: " + input.getAddressId()));
        }
        // Inline address — persist and return
        Address address = Address.builder()
            .user(user)
            .firstName(input.getFirstName())
            .lastName(input.getLastName())
            .addressLine1(input.getAddressLine1())
            .addressLine2(input.getAddressLine2())
            .city(input.getCity())
            .pin(input.getPin())
            .state(input.getState())
            .country(input.getCountry())
            .email(input.getEmail())
            .phoneNumber(input.getPhoneNumber())
            .build();
        return addressRepository.save(address);
    }
}
