package com.app.resmart.dto;

import com.app.resmart.entity.Review;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewDto {
    private Long id;
    private String comment;
    private int rate;
    private Long userId;
    private String username;

    public ReviewDto(Review review) {
        id = review.getId();
        comment = review.getComment();
        rate = review.getRate();
        userId = review.getUser().getId();
        username = review.getUser().getUsername();
    }
}
