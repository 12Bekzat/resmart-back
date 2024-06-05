package com.app.resmart.dto;

import java.util.List;

import com.app.resmart.entity.OrderItem;
import com.app.resmart.entity.OrderStatus;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
public class OrderDto {
    private Long id;
    private float totalPrice;
    private List<OrderItemDto> orderItems;
    private Long restaurant;

    public OrderDto(Long id, float totalPrice, List<OrderItem> orderItems, Long restaurant) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.restaurant = restaurant;
        this.orderItems = orderItems.stream().map((item) -> {
            return new OrderItemDto(item.getId(), item.getQuantity(), item.getPrice(), item.getTotalPrice(), 
            item.getProduct(), item.getSupplier() != null ? item.getSupplier().getId() : null, restaurant, item.getStatus(), item.getPayStatus(), item.getPayWay(), 
            item.getOrderDate(), item.getDeliveryDate());
        }).toList();
    }
}
