package com.codeid.eshopay_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codeid.eshopay_backend.model.entity.CartItem;
import com.codeid.eshopay_backend.model.entity.CartItemId;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, CartItemId> {
    List<CartItem> findByIdCartId(Long cartId);
}