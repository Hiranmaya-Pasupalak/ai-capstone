package com.bookworm.repository;

import com.bookworm.entity.Book;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BookSpecification {

    public static Specification<Book> withFilters(
            String q, String language, String format,
            BigDecimal priceMin, BigDecimal priceMax,
            String categoryId, String authorId) {

        return (root, query, cb) -> {
            if (query != null) {
                query.distinct(true);
            }
            List<Predicate> predicates = new ArrayList<>();

            if (q != null && !q.isBlank()) {
                String pattern = "%" + q.toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
                ));
            }
            if (language != null && !language.isBlank()) {
                predicates.add(cb.equal(root.get("language"), language));
            }
            if (format != null && !format.isBlank()) {
                predicates.add(cb.equal(root.get("format"), format));
            }
            if (priceMin != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), priceMin));
            }
            if (priceMax != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), priceMax));
            }
            if (categoryId != null && !categoryId.isBlank()) {
                Join<Object, Object> categories = root.join("categories");
                predicates.add(cb.equal(categories.get("id"), categoryId));
            }
            if (authorId != null && !authorId.isBlank()) {
                predicates.add(cb.equal(root.get("author").get("id"), authorId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
