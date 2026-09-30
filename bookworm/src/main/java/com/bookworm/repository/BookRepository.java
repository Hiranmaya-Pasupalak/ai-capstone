package com.bookworm.repository;

import com.bookworm.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, String>,
        JpaSpecificationExecutor<Book> {

    Page<Book> findByAuthorId(String authorId, Pageable pageable);

    @Query("""
        SELECT b FROM Book b
        JOIN b.categories c
        WHERE c.id = :categoryId
    """)
    Page<Book> findByCategoryId(@Param("categoryId") String categoryId, Pageable pageable);

    @Query("""
        SELECT b FROM Book b
        JOIN b.categories c
        WHERE c.id IN (
            SELECT c2.id FROM Book b2 JOIN b2.categories c2 WHERE b2.id = :bookId
        )
        AND b.id <> :bookId
    """)
    Page<Book> findRelatedBooks(@Param("bookId") String bookId, Pageable pageable);

    @Query("SELECT b FROM Book b")
    Page<Book> findBestsellers(Pageable pageable);

    @Query("SELECT b FROM Book b")
    Page<Book> findNewLaunches(Pageable pageable);
}
