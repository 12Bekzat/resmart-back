package com.app.resmart.dto;

import lombok.Data;

@Data
public class JwtRequest {
    private String username;
    private String password;
}
