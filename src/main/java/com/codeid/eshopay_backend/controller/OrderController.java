package com.codeid.eshopay_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codeid.eshopay_backend.model.response.ApiResponse;
import com.codeid.eshopay_backend.model.dto.CheckoutReqDTO;
import com.codeid.eshopay_backend.model.dto.OrderDTO;
import com.codeid.eshopay_backend.service.OrderService;
import com.codeid.eshopay_backend.util.SuccessMessage;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RequestMapping("/order")
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/add/{userId}")
    public ResponseEntity<ApiResponse<?>> createOrder(@PathVariable("userId") Long userId,
            @RequestBody CheckoutReqDTO checkoutReqDTO) {
        OrderDTO createOrder = orderService.createOrder(userId, checkoutReqDTO);
        ApiResponse<?> apiResponse = new ApiResponse<>(
                SuccessMessage.Order.CREATE_ORDER_USER, createOrder, LocalDateTime.now(), HttpStatus.OK.value());
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<?>> getOrderById(@PathVariable("orderId") Long orderId) {
        OrderDTO order = orderService.getOrderById(orderId);
        ApiResponse<?> apiResponse = new ApiResponse<>(
                SuccessMessage.Order.GET_ORDER_ID, order, LocalDateTime.now(), HttpStatus.OK.value());
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<?>> getOrderByUserId(@PathVariable("userId") Long userId) {
        List<OrderDTO> response = orderService.getOrdersByUserId(userId);
        ApiResponse<?> apiResponse = new ApiResponse<>(
                SuccessMessage.Order.GET_ORDER_BY_USER, response, LocalDateTime.now(), HttpStatus.OK.value());
        return ResponseEntity.ok(apiResponse);
    }

}