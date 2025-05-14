package com.codeid.eshopay_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codeid.eshopay_backend.model.entity.Product;
import com.codeid.eshopay_backend.model.entity.ProductImage;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Short> {

    List<ProductImage> findAllByProduct(Product product);

    void deleteByProduct(Product product);
}
