package com.elpolloempoderado.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para actualización de datos de usuario
 * Nota: Los campos de dirección fueron removidos.
 * Las direcciones se actualizan a través de /api/addresses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String dni;
    private LocalDate birthDate;
}