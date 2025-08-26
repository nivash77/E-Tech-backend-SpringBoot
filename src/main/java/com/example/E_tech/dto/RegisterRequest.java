package com.example.E_tech.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String role; // optional, default USER
}
