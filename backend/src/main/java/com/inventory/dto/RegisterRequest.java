package com.inventory.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String department;
    private String phoneNumber;
    private String role;
}
