package com.elpolloempoderado.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para crear/actualizar un local/tienda
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalRequest {
    private String nombre;
    private Long distritoId;
    private String direccion;
    private String telefono;
    private String horario;
    private String imagenUrl;
    private String mapsUrl;
}
