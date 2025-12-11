package com.elpolloempoderado.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear o actualizar una dirección
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {
    
    @NotNull(message = "El ID de la ciudad es requerido")
    private Long cityId;
    
    @NotNull(message = "El ID del distrito es requerido")
    private Long districtId;
    
    @NotBlank(message = "La dirección es requerida")
    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String street;
    
    @Size(max = 50, message = "El número no puede exceder 50 caracteres")
    private String number;
    
    @Size(max = 255, message = "La referencia no puede exceder 255 caracteres")
    private String reference;
    
    @NotBlank(message = "El teléfono es requerido")
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String phone;
    
    @Size(max = 50, message = "La etiqueta no puede exceder 50 caracteres")
    private String label;
    
    private Boolean isDefault = false;
}
