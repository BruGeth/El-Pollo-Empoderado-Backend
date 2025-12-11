package com.elpolloempoderado.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String dni;
    private LocalDate birthDate;
    private String address;
    private String telefono;
    private String referenceHome;
    private Long cityId;      // ID de la ciudad (opcional)
    private Long districtId;  // ID del distrito (opcional)
}