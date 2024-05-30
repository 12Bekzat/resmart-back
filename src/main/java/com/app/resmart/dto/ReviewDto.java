package com.app.resmart.dto;

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
}
