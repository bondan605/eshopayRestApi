package com.codeid.eshopay_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codeid.eshopay_backend.model.entity.Carts;

@Repository
public interface CartRepository extends JpaRepository<Carts, Long> {

    Carts findByUserId(Long userId);

    Carts findByCartId(Long cartId);
}