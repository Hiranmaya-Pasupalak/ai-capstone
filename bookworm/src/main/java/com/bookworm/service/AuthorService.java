package com.bookworm.service;

import com.bookworm.dto.*;
import java.util.List;

public interface AuthorService {
    PagedResponse<AuthorDetailDto> listAuthors(String q, int page, int pageSize);
    AuthorDetailDto getAuthor(String authorId);
    PagedResponse<BookSummaryDto> listBooksByAuthor(String authorId, int page, int pageSize);
}
