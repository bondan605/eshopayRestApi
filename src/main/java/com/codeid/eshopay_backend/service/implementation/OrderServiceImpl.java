package com.codeid.eshopay_backend.service.implementation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeid.eshopay_backend.repository.*;
import com.codeid.eshopay_backend.exception.CartNotFoundException;
import com.codeid.eshopay_backend.exception.OrderNotFoundException;
import com.codeid.eshopay_backend.exception.ProductNotFoundException;
import com.codeid.eshopay_backend.model.dto.CartDTO;
import com.codeid.eshopay_backend.model.dto.CartItemDTO;
import com.codeid.eshopay_backend.model.dto.CheckoutReqDTO;
import com.codeid.eshopay_backend.model.dto.OrderDTO;
import com.codeid.eshopay_backend.model.dto.OrderDetailDTO;
import com.codeid.eshopay_backend.model.entity.Bank;
import com.codeid.eshopay_backend.model.entity.Location;
import com.codeid.eshopay_backend.model.entity.OrderDetails;
import com.codeid.eshopay_backend.model.entity.OrderDetailsId;
import com.codeid.eshopay_backend.model.entity.Orders;
import com.codeid.eshopay_backend.model.entity.Shippers;
import com.codeid.eshopay_backend.model.entity.Users;
import com.codeid.eshopay_backend.service.CartService;
import com.codeid.eshopay_backend.service.OrderService;
import com.codeid.eshopay_backend.service.ProductService;
import com.codeid.eshopay_backend.util.ErrorMessage;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ProductService productsService;

    private final ProductRepository productsRepository;

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final UsersRepository usersRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ShippersRepository shipperRepository;
    private final LocationRepository locationRepository;
    private final BankRepository bankRepository;

    private OrderDTO mapToDTO(Orders order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(order.getOrderId());
        orderDTO.setUserId(order.getUsers().getUserId());
        orderDTO.setUserName(order.getUsers().getUserName());

        orderDTO.setOrderDate(order.getOrderDate());
        orderDTO.setRequiredDate(order.getRequiredDate());
        orderDTO.setShippedDate(order.getShippedDate());
        orderDTO.setShipVia(order.getShippers().getShipperId());
        orderDTO.setShipperName(order.getShippers().getCompanyName());
        orderDTO.setFreight(order.getFreight());
        orderDTO.setTotalDiscount(order.getTotalDiscount());
        orderDTO.setTotalAmount(order.getTotalAmount());
        orderDTO.setPaymentType(order.getPaymentType());
        orderDTO.setBankCode(order.getBank().getFintShortName());
        orderDTO.setStreetAddress(order.getLocation().getStreetAddress());
        List<OrderDetails> details = orderDetailRepository.findByIdOrderId(order.getOrderId());

        List<OrderDetailDTO> detailDTOs = details.stream().map(this::mapToOrderDetailDTO).collect(Collectors.toList());
        orderDTO.setOrderDetails(detailDTOs);
        return orderDTO;

    }

    private OrderDetailDTO mapToOrderDetailDTO(OrderDetails orderDetails) {
        OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
        orderDetailDTO.setOrderId(orderDetails.getId().getOrderId());
        orderDetailDTO.setProductId(orderDetails.getProducts().getProductId());
        orderDetailDTO.setProductName(orderDetails.getProducts().getProductName());
        orderDetailDTO.setUnitPrice(orderDetails.getUnitPrice());
        orderDetailDTO.setQuantity(orderDetails.getQuantity());
        orderDetailDTO.setDiscount(orderDetails.getDiscount());
        orderDetailDTO.setSubtotal(orderDetails.getUnitPrice().multiply(BigDecimal.valueOf(orderDetails.getQuantity()))
                .subtract(orderDetails.getDiscount()));
        orderDetailDTO.setSupplierName(orderDetails.getProducts().getSupplier().getCompanyName());
        return orderDetailDTO;
    }

    @Override
    @Transactional
    public OrderDTO createOrder(Long userId, CheckoutReqDTO checkoutReqDTO) {
        try {
            CartDTO cart = cartService.getCartByUserId(userId);
            if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
                throw new CartNotFoundException(ErrorMessage.Cart.CART_EMPTY + userId);
            }

            Users users = usersRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("users not found " + userId));
            Shippers shippers = shipperRepository.findById(checkoutReqDTO.getShipperId()).orElseThrow(
                    () -> new EntityNotFoundException("shippers not found " + checkoutReqDTO.getShipperId()));
            Location location = locationRepository.findById(checkoutReqDTO.getLocationId()).orElseThrow(
                    () -> new EntityNotFoundException("location not found " + checkoutReqDTO.getLocationId()));
            Bank bank = bankRepository.findById(checkoutReqDTO.getBankCode())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "BankCode not found " + checkoutReqDTO.getBankCode()));
            Orders orders = new Orders();
            orders.setUsers(users);
            orders.setShippers(shippers);
            orders.setLocation(location);
            orders.setBank(bank);
            orders.setOrderDate(LocalDateTime.now());
            orders.setRequiredDate(LocalDateTime.now().plusDays(3));
            orders.setPaymentType(checkoutReqDTO.getPaymentType());

            BigDecimal totalDiscount = BigDecimal.ZERO;
            BigDecimal totalAmount = BigDecimal.ZERO;

            Orders saveOrders = orderRepository.save(orders);

            for (CartItemDTO cartItemDTO : cart.getItems()) {
                BigDecimal unitPrice = cartItemDTO.getProductDTO().getUnitPrice();
                if (unitPrice == null) {
                    throw new ProductNotFoundException(
                            ErrorMessage.Product.PRODUCT_UNIT_PRICE_NULL + cartItemDTO.getProductId());
                }
                OrderDetails orderDetails = new OrderDetails();
                OrderDetailsId orderDetailsId = new OrderDetailsId();
                orderDetailsId.setOrderId(saveOrders.getOrderId());
                orderDetailsId.setProductId(cartItemDTO.getProductId());
                orderDetails.setId(orderDetailsId);

                orderDetails.setOrders(saveOrders);
                orderDetails.setProducts(productsRepository.findById(cartItemDTO.getProductId()).orElseThrow(
                        () -> new ProductNotFoundException(
                                ErrorMessage.Product.PRODUCT_NOT_FOUND + cartItemDTO.getProductId())));
                orderDetails.setUnitPrice(cartItemDTO.getProductDTO().getUnitPrice());
                BigDecimal discount = cartItemDTO.getDiscount() != null ? cartItemDTO.getDiscount() : BigDecimal.ZERO;
                orderDetails.setDiscount(discount);
                orderDetails.setQuantity(cartItemDTO.getQuantity());

                orderDetailRepository.save(orderDetails);

                BigDecimal subTotal = unitPrice
                        .multiply(BigDecimal.valueOf(cartItemDTO.getQuantity()))
                        .subtract(discount);
                totalAmount = totalAmount.add(subTotal);
                totalDiscount = totalDiscount.add(discount);

                productsService.updateProductStock(cartItemDTO.getProductId(), cartItemDTO.getQuantity());
            }
            saveOrders.setTotalAmount(totalAmount);
            saveOrders.setTotalDiscount(totalDiscount);
            orderRepository.save(saveOrders);

            for (CartItemDTO cartItemDTO : cart.getItems()) {
                cartService.removeCartItem(cart.getCartId(), cartItemDTO.getProductId());
            }

            return mapToDTO(saveOrders);
        } catch (Exception e) {
            throw new OrderNotFoundException(e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long orderId) {

        Orders orders = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(ErrorMessage.Order.ORDER_NOT_FOUND + orderId));
        return mapToDTO(orders);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        List<Orders> orders = orderRepository.findByUsersUserId(userId);
        return orders.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

}