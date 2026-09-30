package com.bookworm.mapper;

import com.bookworm.dto.*;
import com.bookworm.entity.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EntityMapper {

    // ── Category ─────────────────────────────────────────────────────────────
    public CategoryDto toDto(Category c) {
        return CategoryDto.builder()
            .id(c.getId())
            .name(c.getName())
            .slug(c.getSlug())
            .build();
    }

    // ── Author ────────────────────────────────────────────────────────────────
    public AuthorSummaryDto toSummaryDto(Author a) {
        return AuthorSummaryDto.builder()
            .id(a.getId())
            .name(a.getName())
            .profileImageUrl(a.getProfileImageUrl())
            .build();
    }

    public AuthorDetailDto toDetailDto(Author a) {
        return AuthorDetailDto.builder()
            .id(a.getId())
            .name(a.getName())
            .profileImageUrl(a.getProfileImageUrl())
            .bio(a.getBio())
            .booksCount(a.getBooks() == null ? 0 : a.getBooks().size())
            .build();
    }

    // ── Book ──────────────────────────────────────────────────────────────────
    public BookSummaryDto toSummaryDto(Book b) {
        return BookSummaryDto.builder()
            .id(b.getId())
            .title(b.getTitle())
            .author(toSummaryDto(b.getAuthor()))
            .coverImageUrl(b.getCoverImageUrl())
            .format(b.getFormat())
            .price(b.getPrice())
            .currency(b.getCurrency())
            .categories(b.getCategories().stream().map(this::toDto).collect(Collectors.toList()))
            .rating(b.getAverageRating())
            .estimatedDeliveryDate(LocalDate.now().plusDays(5))
            .build();
    }

    public BookDetailDto toDetailDto(Book b) {
        return BookDetailDto.builder()
            .id(b.getId())
            .title(b.getTitle())
            .author(toDetailDto(b.getAuthor()))
            .coverImageUrl(b.getCoverImageUrl())
            .format(b.getFormat())
            .price(b.getPrice())
            .currency(b.getCurrency())
            .categories(b.getCategories().stream().map(this::toDto).collect(Collectors.toList()))
            .rating(b.getAverageRating())
            .estimatedDeliveryDate(LocalDate.now().plusDays(5))
            .description(b.getDescription())
            .publisher(b.getPublisher())
            .language(b.getLanguage())
            .isbn(b.getIsbn())
            .sellsCount(b.getSellsCount() == null ? 0 : b.getSellsCount())
            .averageRating(b.getAverageRating())
            .reviewCount(b.getReviewCount() == null ? 0 : b.getReviewCount())
            .build();
    }

    // ── Address ───────────────────────────────────────────────────────────────
    public AddressDto toDto(Address a) {
        return AddressDto.builder()
            .id(a.getId())
            .firstName(a.getFirstName())
            .lastName(a.getLastName())
            .addressLine1(a.getAddressLine1())
            .addressLine2(a.getAddressLine2())
            .city(a.getCity())
            .pin(a.getPin())
            .state(a.getState())
            .country(a.getCountry())
            .email(a.getEmail())
            .phoneNumber(a.getPhoneNumber())
            .build();
    }

    public Address toEntity(AddressRequest req, User user) {
        return Address.builder()
            .user(user)
            .firstName(req.getFirstName())
            .lastName(req.getLastName())
            .addressLine1(req.getAddressLine1())
            .addressLine2(req.getAddressLine2())
            .city(req.getCity())
            .pin(req.getPin())
            .state(req.getState())
            .country(req.getCountry())
            .email(req.getEmail())
            .phoneNumber(req.getPhoneNumber())
            .build();
    }

    // ── Review ────────────────────────────────────────────────────────────────
    public ReviewDto toDto(Review r) {
        return ReviewDto.builder()
            .id(r.getId())
            .bookId(r.getBook().getId())
            .reviewerName(r.getUser().getFirstName() + " " + r.getUser().getLastName())
            .rating(r.getRating())
            .comment(r.getComment())
            .createdAt(r.getCreatedAt())
            .build();
    }

    // ── Cart ──────────────────────────────────────────────────────────────────
    public CartItemDto toDto(CartItem ci) {
        return CartItemDto.builder()
            .book(toSummaryDto(ci.getBook()))
            .quantity(ci.getQuantity())
            .itemTotal(ci.getUnitPrice().multiply(java.math.BigDecimal.valueOf(ci.getQuantity())))
            .build();
    }

    // ── Order ─────────────────────────────────────────────────────────────────
    public OrderItemDto toDto(OrderItem oi) {
        return OrderItemDto.builder()
            .book(toSummaryDto(oi.getBook()))
            .quantity(oi.getQuantity())
            .unitPrice(oi.getUnitPrice())
            .itemTotal(oi.getItemTotal())
            .estimatedDeliveryDate(oi.getEstimatedDeliveryDate())
            .build();
    }

    public OrderDto toDto(Order o) {
        return OrderDto.builder()
            .id(o.getId())
            .status(o.getStatus().name().toLowerCase())
            .items(o.getItems().stream().map(this::toDto).collect(Collectors.toList()))
            .deliveryAddress(o.getDeliveryAddress() != null ? toDto(o.getDeliveryAddress()) : null)
            .subtotal(o.getSubtotal())
            .tax(o.getTax())
            .deliveryCharges(o.getDeliveryCharges())
            .discount(o.getDiscount())
            .totalAmount(o.getTotalAmount())
            .currency(o.getCurrency())
            .createdAt(o.getCreatedAt())
            .paymentMethod(o.getPaymentMethod() != null ? o.getPaymentMethod().name().toLowerCase() : null)
            .build();
    }

    // ── User ──────────────────────────────────────────────────────────────────
    public UserProfileDto toDto(User u) {
        return UserProfileDto.builder()
            .id(u.getId())
            .firstName(u.getFirstName())
            .lastName(u.getLastName())
            .email(u.getEmail())
            .phoneNumber(u.getPhoneNumber())
            .giftPointsBalance(u.getGiftPointsBalance())
            .build();
    }
}
