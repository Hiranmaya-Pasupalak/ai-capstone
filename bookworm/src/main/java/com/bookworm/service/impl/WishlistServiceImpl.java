package com.bookworm.service.impl;

import com.bookworm.dto.*;
import com.bookworm.entity.*;
import com.bookworm.exception.*;
import com.bookworm.mapper.EntityMapper;
import com.bookworm.repository.*;
import com.bookworm.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistItemRepository wishlistItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final EntityMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<BookSummaryDto> getWishlist(String userEmail) {
        User user = getUser(userEmail);
        return wishlistItemRepository.findByUserId(user.getId()).stream()
            .map(wi -> mapper.toSummaryDto(wi.getBook()))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addToWishlist(String userEmail, String bookId) {
        User user = getUser(userEmail);
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));
        if (!wishlistItemRepository.existsByUserIdAndBookId(user.getId(), bookId)) {
            wishlistItemRepository.save(WishlistItem.builder().user(user).book(book).build());
        }
    }

    @Override
    @Transactional
    public void removeFromWishlist(String userEmail, String bookId) {
        User user = getUser(userEmail);
        if (!wishlistItemRepository.existsByUserIdAndBookId(user.getId(), bookId)) {
            throw new ResourceNotFoundException("Wishlist item not found: " + bookId);
        }
        wishlistItemRepository.deleteByUserIdAndBookId(user.getId(), bookId);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }
}
