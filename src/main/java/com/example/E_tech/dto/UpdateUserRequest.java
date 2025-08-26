package com.example.E_tech.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String email;
    private String role;
}
