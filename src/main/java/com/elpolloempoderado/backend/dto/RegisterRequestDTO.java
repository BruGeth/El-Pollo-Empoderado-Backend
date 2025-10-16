package com.elpolloempoderado.backend.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class RegisterRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String dni;
    private LocalDate birthDate;
    private String address;
}
