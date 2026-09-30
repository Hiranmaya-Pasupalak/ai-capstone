package com.bookworm.service;

import com.bookworm.dto.*;
import java.math.BigDecimal;
import java.util.List;

public interface BookService {
    PagedResponse<BookSummaryDto> searchBooks(
        String q, String language, String format,
        BigDecimal priceMin, BigDecimal priceMax,
        String categoryId, String authorId,
        String sortBy, int page, int pageSize);

    BookDetailDto getBook(String bookId);

    List<BookSummaryDto> getRelatedBooks(String bookId, int pageSize);

    List<BookSummaryDto> getRecommended(String userEmail, int pageSize);

    List<BookSummaryDto> getBestsellers(int pageSize);

    List<BookSummaryDto> getNewLaunches(int pageSize);
}
