package com.bookworm.service.impl;

import com.bookworm.dto.*;
import com.bookworm.entity.*;
import com.bookworm.exception.*;
import com.bookworm.mapper.EntityMapper;
import com.bookworm.repository.*;
import com.bookworm.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.12");
    private static final int GIFT_POINTS_VALUE = 1; // 1 point = 1 INR (simplification)

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final EntityMapper mapper;

    @Override
    @Transactional
    public CartDto getCart(String userEmail) {
        Cart cart = getOrCreateCart(userEmail);
        return buildCartDto(cart);
    }

    @Override
    @Transactional
    public CartDto addItem(String userEmail, CartItemRequest request) {
        Cart cart = getOrCreateCart(userEmail);
        Book book = bookRepository.findById(request.getBookId())
            .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + request.getBookId()));

        cartItemRepository.findByCartIdAndBookId(cart.getId(), book.getId())
            .ifPresentOrElse(
                existing -> existing.setQuantity(existing.getQuantity() + request.getQuantity()),
                () -> cart.getItems().add(CartItem.builder()
                    .cart(cart).book(book)
                    .quantity(request.getQuantity())
                    .unitPrice(book.getPrice())
                    .build())
            );
        cartRepository.save(cart);
        return buildCartDto(cart);
    }

    @Override
    @Transactional
    public CartDto updateItem(String userEmail, String bookId, int quantity) {
        Cart cart = getOrCreateCart(userEmail);
        CartItem item = cartItemRepository.findByCartIdAndBookId(cart.getId(), bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Item not in cart: " + bookId));
        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return buildCartDto(cart);
    }

    @Override
    @Transactional
    public CartDto removeItem(String userEmail, String bookId) {
        Cart cart = getOrCreateCart(userEmail);
        cartItemRepository.findByCartIdAndBookId(cart.getId(), bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Item not in cart: " + bookId));
        cartItemRepository.deleteByCartIdAndBookId(cart.getId(), bookId);
        cart.getItems().removeIf(i -> i.getBook().getId().equals(bookId));
        return buildCartDto(cart);
    }

    @Override
    @Transactional
    public CartDto applyCoupon(String userEmail, String couponCode) {
        Cart cart = getOrCreateCart(userEmail);
        Coupon coupon = couponRepository.findByCodeAndActiveTrue(couponCode)
            .orElseThrow(() -> new BadRequestException("Invalid or expired coupon: " + couponCode));
        if (coupon.getExpiresAt() != null && coupon.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            throw new BadRequestException("Invalid or expired coupon: " + couponCode);
        }
        cart.setCouponCode(coupon.getCode());
        cart.setCouponDiscount(coupon.getDiscountValue());
        cartRepository.save(cart);
        return buildCartDto(cart);
    }

    @Override
    @Transactional
    public CartDto removeCoupon(String userEmail) {
        Cart cart = getOrCreateCart(userEmail);
        cart.setCouponCode(null);
        cart.setCouponDiscount(BigDecimal.ZERO);
        cartRepository.save(cart);
        return buildCartDto(cart);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private Cart getOrCreateCart(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));
        return cartRepository.findByUserId(user.getId())
            .orElseGet(() -> {
                Cart newCart = Cart.builder().user(user).build();
                return cartRepository.save(newCart);
            });
    }

    CartDto buildCartDto(Cart cart) {
        List<CartItemDto> items = cart.getItems().stream()
            .map(mapper::toDto).collect(Collectors.toList());

        BigDecimal subtotal = cart.getItems().stream()
            .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = subtotal.multiply(TAX_RATE).setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal couponDiscount = cart.getCouponDiscount() != null ? cart.getCouponDiscount() : BigDecimal.ZERO;
        BigDecimal giftDiscount = cart.getGiftPointsDiscount() != null ? cart.getGiftPointsDiscount() : BigDecimal.ZERO;
        BigDecimal total = subtotal.add(tax).subtract(couponDiscount).subtract(giftDiscount)
            .max(BigDecimal.ZERO).setScale(2, java.math.RoundingMode.HALF_UP);

        return CartDto.builder()
            .items(items)
            .subtotal(subtotal)
            .tax(tax)
            .deliveryCharges(BigDecimal.ZERO)
            .couponCode(cart.getCouponCode())
            .couponDiscount(couponDiscount)
            .giftPointsDiscount(giftDiscount)
            .totalAmount(total)
            .currency("INR")
            .build();
    }
}
