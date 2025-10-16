package com.elpolloempoderado.backend.dto;

import java.time.LocalDate;
import java.util.Set;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String dni;
    private LocalDate birthDate;
    private String address;
    private Set<String> roles;
    private String createdAt;
}
