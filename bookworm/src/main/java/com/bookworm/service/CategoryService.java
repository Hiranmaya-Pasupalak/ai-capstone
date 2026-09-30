package com.bookworm.service;

import com.bookworm.dto.CategoryDto;
import com.bookworm.dto.BookSummaryDto;
import com.bookworm.dto.PagedResponse;
import java.math.BigDecimal;
import java.util.List;

public interface CategoryService {
    List<CategoryDto> listAll();
    PagedResponse<BookSummaryDto> listBooksByCategory(
        String categoryId, String language, String format,
        BigDecimal priceMin, BigDecimal priceMax,
        String sortBy, int page, int pageSize);
}
