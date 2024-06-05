package com.app.resmart.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;
    private float price;
    private float totalPrice;
    private OrderStatus status;
    private OrderStatus payStatus;
    private OrderStatus payWay;
    private String orderDate;
    private String deliveryDate;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    private Order order;
    @ManyToOne
    private Product product;
    @ManyToOne
    private User supplier;
    @ManyToOne
    private User restaurant;
}
