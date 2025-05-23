package com.codeid.eshopay_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codeid.eshopay_backend.model.entity.OrderDetails;
import com.codeid.eshopay_backend.model.entity.OrderDetailsId;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetails, OrderDetailsId> {

    List<OrderDetails> findByIdOrderId(Long orderId);
}
