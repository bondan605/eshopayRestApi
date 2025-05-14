package com.codeid.eshopay_backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDto {

    private Short imageId;

    private String fileName;

    private Long fileSize;

    private String fileType;

    private String fileUri;

    private Long productId;
}
