package com.ecommerce.backend.payload;

import lombok.Data;

@Data
public class AuthRequest {
    private String usernameOrEmail;
    private String password;
}
