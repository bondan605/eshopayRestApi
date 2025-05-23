package com.codeid.eshopay_backend.repository.specs;

import org.springframework.data.jpa.domain.Specification;

import com.codeid.eshopay_backend.model.entity.Category;
import com.codeid.eshopay_backend.model.entity.Product;

import jakarta.persistence.criteria.Join;

public class ProductItemSpec {

    // Query -> select * from products where productName like='%keyword%'
    public static Specification<Product> hasKeyWordInProductName(String keyword) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("productName")), "%" + keyword.toLowerCase() + "%");
        };
    }

    public static Specification<Product> hasKeyWordInCategory(String categoryName) {
        return (root, query, criteriaBuilder) -> {
            // join with category
            Join<Product, Category> categoryJoin = root.join("category");

            // create the predicate base on category name
            return criteriaBuilder.like(
                    criteriaBuilder.lower(categoryJoin.get("categoryName")), "%" + categoryName.toLowerCase() + "%");
        };
    }

    public static Specification<Product> searchSpecification(String keyword, String category) {

        Specification<Product> specification = Specification.where(null);

        if (keyword != null && !keyword.isEmpty()) {
            specification = specification.and(hasKeyWordInProductName(keyword));
        }
        if (category != null && !category.isEmpty()) {
            specification = specification.and(hasKeyWordInCategory(category));
        }
        return specification;
    }
}
