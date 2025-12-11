package com.elpolloempoderado.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String dni;
    private LocalDate birthDate;
    private String address;
    private String telefono;
    private String referenceHome;
    private Long cityId;          // ID de la ciudad (opcional)
    private String cityName;      // Nombre de la ciudad (opcional)
    private Long districtId;      // ID del distrito (opcional)
    private String districtName;  // Nombre del distrito (opcional)
    private Set<String> roles;
    private LocalDateTime createdAt;
}