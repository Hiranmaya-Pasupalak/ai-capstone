package com.bookworm.repository;

import com.bookworm.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, String> {
    Page<Author> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
