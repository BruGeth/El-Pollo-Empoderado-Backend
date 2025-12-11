package com.elpolloempoderado.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para respuesta de distrito
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistrictResponse {
    private Long id;
    private String nombre;
    private Long ciudadId;
}
