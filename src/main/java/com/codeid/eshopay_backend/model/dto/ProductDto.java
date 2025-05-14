package com.codeid.eshopay_backend.model.dto;

import java.util.List;

import com.codeid.eshopay_backend.model.entity.ProductImage;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Long productId;

    @Size(max = 40, message = "product name max length is 40")
    private String productName;

    @Size(max = 20, message = "quantity per unit max length is 20")
    private String quantityPerUnit;

    private double unitPrice;

    private Integer unitsInStock;

    private Integer unitsOnOrder;

    private Integer reorderLevel;

    private Integer discontinued;

    private String photo;

    private SuppliersDto supplier;

    private CategoryDto category;
    private List<ProductImage> productImages;
}
