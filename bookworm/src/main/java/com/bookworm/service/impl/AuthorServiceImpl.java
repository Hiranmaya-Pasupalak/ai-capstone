package com.bookworm.service.impl;

import com.bookworm.dto.*;
import com.bookworm.entity.Author;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.mapper.EntityMapper;
import com.bookworm.repository.AuthorRepository;
import com.bookworm.repository.BookRepository;
import com.bookworm.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final EntityMapper mapper;

    @Override
    public PagedResponse<AuthorDetailDto> listAuthors(String q, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Author> authorPage = (q != null && !q.isBlank())
            ? authorRepository.findByNameContainingIgnoreCase(q, pageable)
            : authorRepository.findAll(pageable);

        List<AuthorDetailDto> data = authorPage.getContent().stream()
            .map(mapper::toDetailDto).collect(Collectors.toList());

        return PagedResponse.<AuthorDetailDto>builder()
            .data(data)
            .pagination(PagedResponse.PaginationDto.builder()
                .page(page).pageSize(pageSize)
                .totalItems(authorPage.getTotalElements())
                .totalPages(authorPage.getTotalPages())
                .build())
            .build();
    }

    @Override
    public AuthorDetailDto getAuthor(String authorId) {
        Author author = authorRepository.findById(authorId)
            .orElseThrow(() -> new ResourceNotFoundException("Author not found: " + authorId));
        return mapper.toDetailDto(author);
    }

    @Override
    public PagedResponse<BookSummaryDto> listBooksByAuthor(String authorId, int page, int pageSize) {
        authorRepository.findById(authorId)
            .orElseThrow(() -> new ResourceNotFoundException("Author not found: " + authorId));

        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<com.bookworm.entity.Book> bookPage = bookRepository.findByAuthorId(authorId, pageable);

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
}
