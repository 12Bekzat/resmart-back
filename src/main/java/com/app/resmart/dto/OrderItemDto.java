package com.app.resmart.dto;

import com.app.resmart.entity.OrderStatus;
import com.app.resmart.entity.Product;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long id;
    private int quantity;
    private float price;
    private float totalPrice;
    private ProductDto product;
    private Long supplier;
    private Long restaurant;
    private OrderStatus status;
    private OrderStatus payStatus;
    private OrderStatus payWay;
    private String orderDate;
    private String deliveryDate;

    public OrderItemDto(Long id, int quantity, float price, float totalPrice, Product productDto,
            Long suppLong, Long rest, OrderStatus status, OrderStatus payStatus,OrderStatus payWay,
            String orderDate, String deliveryDate) {
        this.id = id;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = totalPrice;
        this.product = new ProductDto(productDto.getId(),
                productDto.getName(), productDto.getPrice(), productDto.getDiscount(), productDto.getExpiredDate(),
                productDto.getCount(), productDto.getCategory(), productDto.getImageUrl(), productDto.isDeleted(),
                productDto.getReviews().stream().map((item) -> {
                    return new ReviewDto(item);
                }).toList());
        this.supplier = suppLong;
        this.restaurant = rest;
        this.status = status;
        this.payStatus = payStatus;
        this.payWay = payWay;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
    }


    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", quantity='" + getQuantity() + "'" +
            ", price='" + getPrice() + "'" +
            ", totalPrice='" + getTotalPrice() + "'" +
            ", status='" + getStatus() + "'" +
            ", payStatus='" + getPayStatus() + "'" +
            ", payWay='" + getPayWay() + "'" +
            ", orderDate='" + getOrderDate() + "'" +
            ", deliveryDate='" + getDeliveryDate() + "'" +
            "}";
    }

}
