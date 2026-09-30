package com.bookworm.service.impl;

import com.bookworm.dto.*;
import com.bookworm.entity.Book;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.mapper.EntityMapper;
import com.bookworm.repository.*;
import com.bookworm.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final EntityMapper mapper;

    @Override
    public PagedResponse<BookSummaryDto> searchBooks(
            String q, String language, String format,
            BigDecimal priceMin, BigDecimal priceMax,
            String categoryId, String authorId,
            String sortBy, int page, int pageSize) {

        Pageable pageable = buildPageable(page, pageSize, sortBy);
        var spec = BookSpecification.withFilters(q, language, format, priceMin, priceMax, categoryId, authorId);
        Page<Book> bookPage = bookRepository.findAll(spec, pageable);

        List<BookSummaryDto> data = bookPage.getContent().stream()
            .map(mapper::toSummaryDto).collect(Collectors.toList());

        return PagedResponse.<BookSummaryDto>builder()
            .data(data)
            .pagination(PagedResponse.PaginationDto.builder()
                .page(page).pageSize(pageSize)
                .totalItems(bookPage.getTotalElements())
                .totalPages(bookPage.getTotalPages())
                .build())
            .build();
    }

    @Override
    public BookDetailDto getBook(String bookId) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));
        return mapper.toDetailDto(book);
    }

    @Override
    public List<BookSummaryDto> getRelatedBooks(String bookId, int pageSize) {
        bookRepository.findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));
        return bookRepository.findRelatedBooks(bookId, PageRequest.of(0, pageSize))
            .getContent().stream().map(mapper::toSummaryDto).collect(Collectors.toList());
    }

    @Override
    public List<BookSummaryDto> getRecommended(String userEmail, int pageSize) {
        // Simplified: returns bestsellers as recommendations baseline
        Pageable pageable = PageRequest.of(0, pageSize, Sort.by("sellsCount").descending());
        return bookRepository.findBestsellers(pageable)
            .getContent().stream().map(mapper::toSummaryDto).collect(Collectors.toList());
    }

    @Override
    public List<BookSummaryDto> getBestsellers(int pageSize) {
        Pageable pageable = PageRequest.of(0, pageSize, Sort.by("sellsCount").descending());
        return bookRepository.findBestsellers(pageable)
            .getContent().stream().map(mapper::toSummaryDto).collect(Collectors.toList());
    }

    @Override
    public List<BookSummaryDto> getNewLaunches(int pageSize) {
        Pageable pageable = PageRequest.of(0, pageSize, Sort.by("id").descending());
        return bookRepository.findNewLaunches(pageable)
            .getContent().stream().map(mapper::toSummaryDto).collect(Collectors.toList());
    }

    private Pageable buildPageable(int page, int pageSize, String sortBy) {
        Sort sort = switch (sortBy == null ? "relevance" : sortBy) {
            case "price_asc"   -> Sort.by("price").ascending();
            case "price_desc"  -> Sort.by("price").descending();
            case "bestselling" -> Sort.by("sellsCount").descending();
            case "newest"      -> Sort.by("id").descending();
            case "rating"      -> Sort.by("averageRating").descending();
            default            -> Sort.unsorted();
        };
        return PageRequest.of(page - 1, pageSize, sort);
    }
}
