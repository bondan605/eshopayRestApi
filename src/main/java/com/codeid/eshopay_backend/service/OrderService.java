package com.codeid.eshopay_backend.service;

import java.util.List;

import com.codeid.eshopay_backend.model.dto.CheckoutReqDTO;
import com.codeid.eshopay_backend.model.dto.OrderDTO;

public interface OrderService {
    OrderDTO createOrder(Long cartId, CheckoutReqDTO checkoutReqDTO);

    OrderDTO getOrderById(Long orderId);

    List<OrderDTO> getOrdersByUserId(Long userId);

}