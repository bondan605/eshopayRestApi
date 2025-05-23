package com.codeid.eshopay_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codeid.eshopay_backend.model.response.ApiResponse;
import com.codeid.eshopay_backend.model.dto.AddItemsToCartRequest;
import com.codeid.eshopay_backend.model.dto.CartDTO;
import com.codeid.eshopay_backend.service.CartService;
import com.codeid.eshopay_backend.util.SuccessMessage;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<?>> getCartById(@PathVariable("userId") Long userId) {

        CartDTO response = cartService.getCartByUserId(userId);
        ApiResponse<?> apiResponse = new ApiResponse<>(
                SuccessMessage.Cart.GET_CART_USER, response, LocalDateTime.now(),
                SuccessMessage.Http.OK);

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/create/{userId}")
    public ResponseEntity<ApiResponse<?>> addCart(@PathVariable("userId") Long userId) {
        CartDTO response = cartService.createCart(userId);
        ApiResponse<?> apiResponse = new ApiResponse<>(
                SuccessMessage.Cart.CREATE_CART_USER, response, LocalDateTime.now(),
                SuccessMessage.Http.CREATED);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/{cartId}/add")
    public ResponseEntity<ApiResponse<?>> addItemToCart(@PathVariable("cartId") Long cartId,
            @RequestBody AddItemsToCartRequest request) {

        CartDTO response = cartService.addItemToCart(cartId, request.getItems());
        ApiResponse<?> apiResponse = new ApiResponse<>(
                SuccessMessage.Cart.ADD_CART_ITEM, response, LocalDateTime.now(),
                SuccessMessage.Http.CREATED);
        return ResponseEntity.ok(apiResponse);

    }

    @PutMapping("/{cartId}/{productId}/update")
    public ResponseEntity<ApiResponse<?>> updateCartItem(
            @PathVariable("cartId") Long cartId,
            @PathVariable("productId") Long productId,
            @RequestParam Integer quantity) {

        CartDTO response = cartService.updateCartItem(cartId, productId, quantity);

        ApiResponse<?> apiResponse = new ApiResponse<>(
                SuccessMessage.Cart.UPDATE_CART_ITEM, response, LocalDateTime.now(),
                HttpStatus.OK.value());

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{cartId}/{productId}/remove")
    public ResponseEntity<ApiResponse<?>> removeCartItem(
            @PathVariable("cartId") Long cartId,
            @PathVariable("productId") Long productId) {

        cartService.removeCartItem(cartId, productId);

        ApiResponse<?> apiResponse = new ApiResponse<>(
                SuccessMessage.Cart.REMOVE_CART_ITEM, null, LocalDateTime.now(), HttpStatus.OK.value());

        return ResponseEntity.ok(apiResponse);
    }
}
