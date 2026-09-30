package com.bookworm.service;

import com.bookworm.dto.*;
import java.util.List;

public interface WishlistService {
    List<BookSummaryDto> getWishlist(String userEmail);
    void addToWishlist(String userEmail, String bookId);
    void removeFromWishlist(String userEmail, String bookId);
}
