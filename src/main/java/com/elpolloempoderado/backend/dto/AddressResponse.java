package com.elpolloempoderado.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para retornar información de una dirección
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    
    private Long id;
    private Long userId;
    private CityResponse city;
    private DistrictResponse district;
    private String street;
    private String number;
    private String reference;
    private String phone;
    private String label;
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Retorna la dirección completa formateada
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(street);
        if (number != null && !number.isEmpty()) {
            sb.append(" ").append(number);
        }
        sb.append(", ").append(district.getNombre());
        sb.append(", ").append(city.getNombre());
        if (reference != null && !reference.isEmpty()) {
            sb.append(" (Ref: ").append(reference).append(")");
        }
        return sb.toString();
    }
}
