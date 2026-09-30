package com.bookworm.controller;

import com.bookworm.dto.*;
import com.bookworm.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<PagedResponse<BookSummaryDto>> searchBooks(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String format,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String authorId,
            @RequestParam(required = false, defaultValue = "relevance") String sortBy,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(bookService.searchBooks(
            q, language, format, priceMin, priceMax, categoryId, authorId, sortBy, page, pageSize));
    }

    @GetMapping("/sections/recommended")
    public ResponseEntity<List<BookSummaryDto>> getRecommended(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "20") int pageSize) {
        String email = (userDetails != null) ? userDetails.getUsername() : null;
        return ResponseEntity.ok(bookService.getRecommended(email, pageSize));
    }

    @GetMapping("/sections/bestsellers")
    public ResponseEntity<List<BookSummaryDto>> getBestsellers(
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(bookService.getBestsellers(pageSize));
    }

    @GetMapping("/sections/new-launches")
    public ResponseEntity<List<BookSummaryDto>> getNewLaunches(
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(bookService.getNewLaunches(pageSize));
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookDetailDto> getBook(@PathVariable String bookId) {
        return ResponseEntity.ok(bookService.getBook(bookId));
    }

    @GetMapping("/{bookId}/related")
    public ResponseEntity<List<BookSummaryDto>> getRelatedBooks(
            @PathVariable String bookId,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(bookService.getRelatedBooks(bookId, pageSize));
    }
}
