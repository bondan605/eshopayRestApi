package com.codeid.eshopay_backend.service.implementation;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.naming.InsufficientResourcesException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeid.eshopay_backend.repository.CartItemRepository;
import com.codeid.eshopay_backend.repository.CartRepository;
import com.codeid.eshopay_backend.repository.ProductRepository;
import com.codeid.eshopay_backend.exception.CartItemNotFoundException;
import com.codeid.eshopay_backend.exception.CartNotFoundException;
import com.codeid.eshopay_backend.exception.ProductNotFoundException;
import com.codeid.eshopay_backend.model.dto.CartDTO;
import com.codeid.eshopay_backend.model.dto.CartItemDTO;
import com.codeid.eshopay_backend.model.dto.CartItemRequestDTO;
import com.codeid.eshopay_backend.model.entity.CartItem;
import com.codeid.eshopay_backend.model.entity.CartItemId;
import com.codeid.eshopay_backend.model.entity.Carts;
import com.codeid.eshopay_backend.model.entity.Product;
import com.codeid.eshopay_backend.service.CartService;
import com.codeid.eshopay_backend.util.ErrorMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final ProductRepository productsRepository;

    private final CartItemRepository cartItemRepository;

    public static CartDTO mapToDTO(Carts carts) {
        List<CartItemDTO> itemDTOs = carts.getCartItems().stream().map(item -> {
            CartItemDTO dto = new CartItemDTO();
            dto.setCartId(item.getId().getCartId());
            dto.setProductId(item.getId().getProductId());
            dto.setQuantity(item.getQuantity());
            dto.setUnitPrice(item.getUnitPrice());
            dto.setDiscount(item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO);
            BigDecimal priceBeforeDiscount = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            BigDecimal discountAmount = priceBeforeDiscount.multiply(item.getDiscount());
            dto.setSubTotal(priceBeforeDiscount.subtract(discountAmount));
            dto.setProductDTO(ProductServiceImpl.mapToDto(item.getProducts()));
            return dto;
        }).collect(Collectors.toList());
        Integer totalItems = carts.getCartItems().stream().mapToInt(CartItem::getQuantity).sum();

        return new CartDTO(
                carts.getCartId(), carts.getUserId(), itemDTOs, totalItems);
    }

    public static Carts mapToModel(CartDTO cartDTO) {
        Carts carts = new Carts();
        carts.setCartId(cartDTO.getCartId());
        carts.setUserId(cartDTO.getUserId());

        if (cartDTO.getItems() != null) {
            List<CartItem> cartItems = cartDTO.getItems().stream().map(itemDto -> {
                CartItem item = new CartItem();

                CartItemId itemId = new CartItemId();
                itemId.setCartId(cartDTO.getCartId());
                itemId.setProductId(itemDto.getProductId());

                item.setId(itemId);
                item.setQuantity(itemDto.getQuantity());
                item.setDiscount(itemDto.getDiscount());
                item.setUnitPrice(itemDto.getUnitPrice());
                item.setProducts(ProductServiceImpl.mapToEntity(itemDto.getProductDTO()));
                item.setCarts(carts);
                return item;
            }).collect(Collectors.toList());

            carts.setCartItems(cartItems);
        }

        return carts;
    }

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCartByUserId(Long userId) {
        try {
            Carts foundCarts = cartRepository.findByUserId(userId);
            if (foundCarts == null) {
                throw new RuntimeException("No cart found for user ID: " + userId);
            }
            return mapToDTO(foundCarts);
        } catch (Exception e) {
            throw new RuntimeException("error to get carts id : " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public CartDTO createCart(Long userId) {
        try {
            Carts foundCarts = cartRepository.findByUserId(userId);
            if (foundCarts != null) {
                throw new CartNotFoundException(ErrorMessage.Cart.CART_ALREADY_EXIST);
            }

            Carts newCarts = new Carts();
            newCarts.setUserId(userId);
            newCarts.setCreateDate(Instant.now());
            newCarts.setCartItems(new ArrayList<>());

            Carts saved = cartRepository.save(newCarts);
            return mapToDTO(saved);

        } catch (Exception e) {
            throw new CartNotFoundException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public CartDTO addItemToCart(Long cartId, List<CartItemRequestDTO> items) {

        Carts foundCarts = cartRepository.findByCartId(cartId);
        if (foundCarts == null) {
            throw new CartNotFoundException(ErrorMessage.Cart.CART_NOT_FOUND);
        }

        for (CartItemRequestDTO item : items) {
            Long productId = item.getProductId();
            Integer quantity = item.getQuantity();

            // Validasi quantity
            if (quantity == null || quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0 for product ID: " + productId);
            }

            Product foundProducts = productsRepository.findByProductId(productId);
            if (foundProducts == null) {
                Optional<Product> regularProduct = productsRepository.findById(productId);
                if (!regularProduct.isPresent()) {
                    throw new ProductNotFoundException(ErrorMessage.Product.PRODUCT_NOT_EXIST);
                }
                throw new ProductNotFoundException(ErrorMessage.Product.PRODUCT_DELETED);
            }

            // Cek stok cukup
            int currentStock = foundProducts.getUnitsInStock();

            CartItemId itemId = new CartItemId();
            itemId.setCartId(foundCarts.getCartId());
            itemId.setProductId(productId);
            Optional<CartItem> existingItem = cartItemRepository.findById(itemId);

            CartItem cartItem;
            if (existingItem.isPresent()) {
                cartItem = existingItem.get();
                int newQuantity = cartItem.getQuantity() + quantity;

                if (currentStock < newQuantity) {
                    throw new ProductNotFoundException(
                            ErrorMessage.Product.PRODUCT_STOCK_INSUNFFICIENT + productId);
                }

                cartItem.setQuantity(newQuantity);
                cartItem.setDiscount(BigDecimal.valueOf(0.05));
                cartItem.setModifiedDate(Instant.now());
            } else {
                if (currentStock < quantity) {
                    throw new ProductNotFoundException(
                            ErrorMessage.Product.PRODUCT_STOCK_INSUNFFICIENT + productId);
                }
                cartItem = new CartItem();
                cartItem.setId(itemId);
                cartItem.setCarts(foundCarts);
                cartItem.setProducts(foundProducts);
                cartItem.setQuantity(quantity);
                cartItem.setCreateDate(Instant.now());
                cartItem.setUnitPrice(foundProducts.getUnitPrice());
                cartItem.setDiscount(BigDecimal.valueOf(0.05));
            }

            cartItemRepository.save(cartItem);
        }

        cartItemRepository.flush();

        Carts updateCart = cartRepository.findByCartId(foundCarts.getCartId());
        return mapToDTO(updateCart);
    }

    @Override
    @Transactional
    public CartDTO updateCartItem(Long cartId, Long productId, Integer quantity) {
        CartItemId itemId = new CartItemId(cartId, productId);
        Optional<CartItem> foundCartItem = cartItemRepository.findById(itemId);
        if (!foundCartItem.isPresent()) {
            throw new CartNotFoundException(ErrorMessage.Cart.CART_NOT_FOUND);
        }
        CartItem item = foundCartItem.get();
        item.setQuantity(quantity);
        cartItemRepository.save(item);

        return mapToDTO(item.getCarts());
    }

    @Override
    @Transactional
    public void removeCartItem(Long cartId, Long productId) {

        CartItemId itemId = new CartItemId(cartId, productId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CartItemNotFoundException(ErrorMessage.CartItem.CART_ITEM_NOT_FOUND));

        cartItemRepository.delete(item);
    }
}
