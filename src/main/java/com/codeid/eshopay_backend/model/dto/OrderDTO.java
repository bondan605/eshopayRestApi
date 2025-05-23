package com.codeid.eshopay_backend.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long orderId;

    private Long userId;
    private String userName;

    private LocalDateTime orderDate;
    private LocalDateTime requiredDate;
    private LocalDateTime shippedDate;

    private Long shipVia;
    private String shipperName;

    private Double freight;
    private BigDecimal totalDiscount;
    private BigDecimal totalAmount;

    private String paymentType;
    private String bankCode;
    private String orderStatus;

    private String streetAddress;

    private List<OrderDetailDTO> orderDetails;
}
