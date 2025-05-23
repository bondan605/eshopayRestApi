package com.codeid.eshopay_backend.model.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "products", schema = "oe")
public class Product extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @NonNull
    @Column(name = "product_name")
    private String productName;

    @Column(name = "quantity_per_unit")
    private String quantityPerUnit;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "units_in_stock")
    private Integer unitsInStock;

    @Column(name = "units_on_order")
    private Integer unitsOnOrder;

    @Column(name = "reorder_level")
    private Integer reorderLevel;

    @Column(name = "discontinued")
    @NonNull
    private Integer discontinued;

    @Column(name = "photo")
    private String photo;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Suppliers supplier;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
