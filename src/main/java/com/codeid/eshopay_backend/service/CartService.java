package com.codeid.eshopay_backend.service;

import java.util.List;

import com.codeid.eshopay_backend.model.dto.CartDTO;
import com.codeid.eshopay_backend.model.dto.CartItemRequestDTO;

public interface CartService {

    CartDTO getCartByUserId(Long userId);

    CartDTO createCart(Long userId);

    CartDTO addItemToCart(Long cartId, List<CartItemRequestDTO> items);

    CartDTO updateCartItem(Long cartId, Long productId, Integer quantity);

    void removeCartItem(Long cartId, Long productId);
}