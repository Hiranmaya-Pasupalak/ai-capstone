package com.bookworm.controller;

import com.bookworm.dto.*;
import com.bookworm.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<PagedResponse<AuthorDetailDto>> listAuthors(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(authorService.listAuthors(q, page, pageSize));
    }

    @GetMapping("/{authorId}")
    public ResponseEntity<AuthorDetailDto> getAuthor(@PathVariable String authorId) {
        return ResponseEntity.ok(authorService.getAuthor(authorId));
    }

    @GetMapping("/{authorId}/books")
    public ResponseEntity<PagedResponse<BookSummaryDto>> listBooksByAuthor(
            @PathVariable String authorId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(authorService.listBooksByAuthor(authorId, page, pageSize));
    }
}
