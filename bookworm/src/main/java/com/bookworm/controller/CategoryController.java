package com.bookworm.controller;

import com.bookworm.dto.*;
import com.bookworm.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> listCategories() {
        return ResponseEntity.ok(categoryService.listAll());
    }

    @GetMapping("/{categoryId}/books")
    public ResponseEntity<PagedResponse<BookSummaryDto>> listBooksByCategory(
            @PathVariable String categoryId,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String format,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false, defaultValue = "relevance") String sortBy,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(categoryService.listBooksByCategory(
            categoryId, language, format, priceMin, priceMax, sortBy, page, pageSize));
    }
}
