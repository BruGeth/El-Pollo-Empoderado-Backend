package com.elpolloempoderado.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DishRequest {
    
    @NotBlank(message = "El nombre del plato es requerido")
    private String name;
    
    private String description;
    
    @NotNull(message = "El precio es requerido")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal price;
    
    private String imageUrl;
    
    @NotNull(message = "La categoría es requerida")
    private Long categoryId;
}
