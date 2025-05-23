package com.codeid.eshopay_backend.model.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cart_items", schema = "oe")
public class CartItem extends AbstractEntity {
    @EmbeddedId
    private CartItemId id;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "discount")
    private BigDecimal discount;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product products;

    @ManyToOne
    @MapsId("cartId")
    @JoinColumn(name = "cart_id")
    private Carts carts;
}