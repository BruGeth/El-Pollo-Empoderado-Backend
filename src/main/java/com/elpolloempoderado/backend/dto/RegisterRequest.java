package com.elpolloempoderado.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para registro de nuevos usuarios
 * Nota: Los campos de dirección (address, telefono, referenceHome, cityId, districtId)
 * fueron removidos. Las direcciones ahora se manejan a través de /api/addresses
 * después del registro del usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String dni;
    private LocalDate birthDate;
}