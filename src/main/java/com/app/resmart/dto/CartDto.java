package com.app.resmart.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartDto {
    private Long id;
    private float totalPrice;
    private List<ProductDto> productDtos;
}
