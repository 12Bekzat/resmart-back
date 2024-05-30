package com.app.resmart.dto;

import lombok.Data;

@Data
public class RegUserDto {
    private String username;
    private String password;
    private String email;
    private String contactPerson;
    private String contactPhone;
    private String name;
    private String address;
    private String workTime;
    private String descText;
    private String logotype;
    private String role;


    @Override
    public String toString() {
        return "{" +
            " username='" + getUsername() + "'" +
            ", password='" + getPassword() + "'" +
            ", email='" + getEmail() + "'" +
            ", contactPerson='" + getContactPerson() + "'" +
            ", name='" + getName() + "'" +
            ", address='" + getAddress() + "'" +
            ", descText='" + getDescText() + "'" +
            ", logotype='" + getLogotype() + "'" +
            ", role='" + getRole() + "'" +
            "}";
    }
}

