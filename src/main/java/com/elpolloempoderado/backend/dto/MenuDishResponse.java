package com.elpolloempoderado.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO simplificado de plato para el menú (sin incluir categoría para evitar redundancia)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuDishResponse {
    
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
}
