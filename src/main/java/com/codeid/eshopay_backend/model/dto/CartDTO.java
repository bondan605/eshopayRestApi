package com.codeid.eshopay_backend.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {

    private Long cartId;
    private Long userId;
    private List<CartItemDTO> items;
    private Integer totalItems;

}
