package com.elpolloempoderado.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para respuesta de ciudad
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityResponse {
    private Long id;
    private String nombre;
}
