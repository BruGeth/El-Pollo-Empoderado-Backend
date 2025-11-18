package com.elpolloempoderado.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long expiresIn;
    private UserDTO user;
    
    public AuthResponse(String token, Long expiresIn, UserDTO user) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.user = user;
    }
}