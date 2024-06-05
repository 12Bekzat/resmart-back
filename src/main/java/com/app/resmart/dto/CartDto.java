package com.app.resmart.dto;

import java.util.List;

import com.app.resmart.entity.Order;
import lombok.Data;

@Data
public class CartDto {
    private Long id;
    private float totalPrice;
    private List<OrderDto> productDtos;

    public CartDto(Long id, float totalPrice, List<Order> productDtos) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.productDtos = productDtos.stream().map((item) -> {
            return new OrderDto(item.getId(), item.getTotalPrice(), item.getOrderItems(), item.getRestaurant().getId());
        }).toList();
    }
}
