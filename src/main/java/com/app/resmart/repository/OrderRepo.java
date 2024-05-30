package com.app.resmart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.resmart.entity.Order;

public interface OrderRepo extends JpaRepository<Order, Long> {

}
