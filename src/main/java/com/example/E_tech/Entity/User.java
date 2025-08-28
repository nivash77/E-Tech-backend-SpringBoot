package com.example.E_tech.Entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    private String role;
    private boolean verified = false;

    private String verificationCode;
    private LocalDateTime verificationCodeExpiry;

    @CreatedDate
    private LocalDateTime createdAt;
}
