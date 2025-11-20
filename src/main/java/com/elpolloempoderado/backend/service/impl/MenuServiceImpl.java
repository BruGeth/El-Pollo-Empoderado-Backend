package com.elpolloempoderado.backend.service.impl;

import com.elpolloempoderado.backend.dto.MenuCategoryResponse;
import com.elpolloempoderado.backend.dto.MenuDishResponse;
import com.elpolloempoderado.backend.model.Category;
import com.elpolloempoderado.backend.model.Dish;
import com.elpolloempoderado.backend.repository.MenuRepository;
import com.elpolloempoderado.backend.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuServiceImpl implements MenuService {
    
    private final MenuRepository menuRepository;
    
    @Override
    public List<MenuCategoryResponse> getFullMenu() {
        // Obtener todas las categorías con sus platos en una sola consulta optimizada
        List<Category> categories = menuRepository.findAllCategoriesWithDishes();
        
        // Convertir entidades a DTOs
        return categories.stream()
                .map(this::convertToMenuCategoryResponse)
                .sorted(Comparator.comparing(MenuCategoryResponse::getName)) // Ordenar categorías por nombre
                .collect(Collectors.toList());
    }
    
    /**
     * Convierte una entidad Category a MenuCategoryResponse
     */
    private MenuCategoryResponse convertToMenuCategoryResponse(Category category) {
        List<MenuDishResponse> dishResponses = category.getDishes().stream()
                .map(this::convertToMenuDishResponse)
                .sorted(Comparator.comparing(MenuDishResponse::getName)) // Ordenar platos por nombre
                .collect(Collectors.toList());
        
        return new MenuCategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                dishResponses
        );
    }
    
    /**
     * Convierte una entidad Dish a MenuDishResponse
     */
    private MenuDishResponse convertToMenuDishResponse(Dish dish) {
        return new MenuDishResponse(
                dish.getId(),
                dish.getName(),
                dish.getDescription(),
                dish.getPrice(),
                dish.getImageUrl()
        );
    }
}
