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

    // private Long userId;

    private Long shipperId;
    // private String shipName;
    // private String shipAddress;
    // private String shipCity;
    // private String shipRegion;
    // private String shipPostalCode;
    // private String shipCountry;

    private String paymentType;

    private String bankCode;

    private Long locationId;
}
