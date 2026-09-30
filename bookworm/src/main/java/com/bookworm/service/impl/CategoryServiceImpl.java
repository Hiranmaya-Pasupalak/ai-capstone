package com.bookworm.service.impl;

import com.bookworm.dto.*;
import com.bookworm.entity.Category;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.mapper.EntityMapper;
import com.bookworm.repository.BookRepository;
import com.bookworm.repository.BookSpecification;
import com.bookworm.repository.CategoryRepository;
import com.bookworm.service.CategoryService;
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
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final EntityMapper mapper;

    @Override
    public List<CategoryDto> listAll() {
        return categoryRepository.findAll().stream()
            .map(mapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public PagedResponse<BookSummaryDto> listBooksByCategory(
            String categoryId, String language, String format,
            BigDecimal priceMin, BigDecimal priceMax,
            String sortBy, int page, int pageSize) {

        categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));

        Pageable pageable = buildPageable(page, pageSize, sortBy);
        Page<com.bookworm.entity.Book> bookPage = bookRepository.findByCategoryId(categoryId, pageable);

        return buildPagedResponse(bookPage, page, pageSize);
    }

    private Pageable buildPageable(int page, int pageSize, String sortBy) {
        Sort sort = switch (sortBy == null ? "relevance" : sortBy) {
            case "price_asc"    -> Sort.by("price").ascending();
            case "price_desc"   -> Sort.by("price").descending();
            case "bestselling"  -> Sort.by("sellsCount").descending();
            case "rating"       -> Sort.by("averageRating").descending();
            default             -> Sort.unsorted();
        };
        return PageRequest.of(page - 1, pageSize, sort);
    }

    private PagedResponse<BookSummaryDto> buildPagedResponse(Page<com.bookworm.entity.Book> bookPage, int page, int pageSize) {
        List<BookSummaryDto> data = bookPage.getContent().stream()
            .map(mapper::toSummaryDto)
            .collect(Collectors.toList());

        return PagedResponse.<BookSummaryDto>builder()
            .data(data)
            .pagination(PagedResponse.PaginationDto.builder()
                .page(page)
                .pageSize(pageSize)
                .totalItems(bookPage.getTotalElements())
                .totalPages(bookPage.getTotalPages())
                .build())
            .build();
    }
}
