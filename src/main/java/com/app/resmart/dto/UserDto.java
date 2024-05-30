package com.app.resmart.dto;

import com.app.resmart.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private String email;
    private boolean isBanned;
    private String contactPerson;
    private String phone;
    private String name;
    private String address;
    private String descText;
    private String logotype;
    private String workTime;
    private String createdAt;
    private CartDto cart;
    private String role;
}
