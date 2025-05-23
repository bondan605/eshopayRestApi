package com.codeid.eshopay_backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutReqDTO {

    private Long shipperId;

    private String paymentType;

    private String bankCode;

    private Long locationId;
}
