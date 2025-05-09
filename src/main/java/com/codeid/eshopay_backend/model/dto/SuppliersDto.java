package com.codeid.eshopay_backend.model.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuppliersDto {
    private Long suppliersId;

    @Size(max = 40, message = "Length value must not exceeded than 40")
    private String companyName;
}
