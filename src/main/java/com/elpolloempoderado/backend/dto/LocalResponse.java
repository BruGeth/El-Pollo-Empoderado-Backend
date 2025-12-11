package com.elpolloempoderado.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para respuesta de local/tienda
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalResponse {
    private Long id;
    private String nombre;
    private Long distritoId;
    private String distritoNombre;
    private Long ciudadId;
    private String ciudadNombre;
    private String direccion;
    private String telefono;
    private String horario;
    private String imagenUrl;
    private String mapsUrl;
    private LocalDateTime createdAt;
}
