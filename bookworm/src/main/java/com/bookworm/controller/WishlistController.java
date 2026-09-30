package com.bookworm.controller;

import com.bookworm.dto.*;
import com.bookworm.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<BookSummaryDto>> getWishlist(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(wishlistService.getWishlist(userDetails.getUsername()));
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<Void> addToWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String bookId) {
        wishlistService.addToWishlist(userDetails.getUsername(), bookId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> removeFromWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String bookId) {
        wishlistService.removeFromWishlist(userDetails.getUsername(), bookId);
        return ResponseEntity.noContent().build();
    }
}
