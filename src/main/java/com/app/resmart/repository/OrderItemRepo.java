package com.app.resmart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.resmart.entity.OrderItem;

public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {

}
