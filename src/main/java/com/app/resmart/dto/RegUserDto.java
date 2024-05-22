package com.app.resmart.dto;

import lombok.Data;

@Data
public class RegUserDto {
    private String username;
    private String password;
    private String email;
    private String contactPerson;
    private String name;
    private String address;
    private String descText;
    private String logotype;
    private String createdAt;
    private String role;
}
