package com.app.resmart.dto;

import java.util.List;

import com.app.resmart.entity.Review;
import com.app.resmart.entity.User;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private float price;
    private float discount;
    private String expiredDate;
    private int count;
    private String category;
    private String imageUrl;
    private boolean deleted;

    private List<ReviewDto> reviews;
}
