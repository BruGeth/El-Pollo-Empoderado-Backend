package com.elpolloempoderado.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO de respuesta con datos del usuario
 * Nota: Los campos address, telefono, referenceHome, cityId, cityName, districtId, districtName
 * están marcados como @Deprecated y siempre retornan null.
 * Para obtener las direcciones del usuario, usar GET /api/addresses
 */
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
    
    @Deprecated // Usar GET /api/addresses para obtener direcciones del usuario
    private String address;
    @Deprecated
    private String telefono;
    @Deprecated
    private String referenceHome;
    @Deprecated
    private Long cityId;
    @Deprecated
    private String cityName;
    @Deprecated
    private Long districtId;
    @Deprecated
    private String districtName;
    
    private Set<String> roles;
    private LocalDateTime createdAt;
}