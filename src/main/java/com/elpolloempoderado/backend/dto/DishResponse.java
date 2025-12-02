package com.elpolloempoderado.backend.dto;

import com.elpolloempoderado.backend.model.Dish;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DishResponse {
    
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice; // Precio anterior para mostrar descuentos
    private String imageUrl;
    private Long categoryId;
    private String categoryName;

    public static DishResponse fromEntity(Dish dish) {
        if (dish == null) return null;
        
        DishResponse response = new DishResponse();
        response.setId(dish.getId());
        response.setName(dish.getName());
        response.setDescription(dish.getDescription());
        response.setPrice(dish.getPrice());
        response.setOriginalPrice(dish.getOriginalPrice());
        response.setImageUrl(dish.getImageUrl());
        
        // Incluir información de la categoría si está presente
        if (dish.getCategory() != null) {
            response.setCategoryId(dish.getCategory().getId());
            response.setCategoryName(dish.getCategory().getName());
        }
        
        return response;
    }
}
