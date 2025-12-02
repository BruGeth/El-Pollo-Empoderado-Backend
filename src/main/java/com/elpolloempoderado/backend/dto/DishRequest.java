package com.elpolloempoderado.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DishRequest {
    
    @NotBlank(message = "El nombre del plato es requerido")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String name;
    
    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    private String description;
    
    @NotNull(message = "El precio es requerido")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal price;
    
    @DecimalMin(value = "0.01", message = "El precio original debe ser mayor a 0")
    private BigDecimal originalPrice; // Opcional: precio antes del descuento
    
    private String imageUrl;
    
    @NotNull(message = "La categoría es requerida")
    private Long categoryId;
}
