package com.codeid.eshopay_backend.model.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemId implements Serializable {

    @Column(name = "cart_id")
    private Long cartId;

    @Column(name = "product_id")
    private Long productId;
}