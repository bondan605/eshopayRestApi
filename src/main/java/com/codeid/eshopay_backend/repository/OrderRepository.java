package com.codeid.eshopay_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codeid.eshopay_backend.model.entity.Orders;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

    List<Orders> findByUsersUserId(Long userId);
}
