package com.elpolloempoderado.backend.service;

import com.elpolloempoderado.backend.dto.DishRequest;
import com.elpolloempoderado.backend.dto.DishResponse;

import java.util.List;

public interface DishService {
    
    /**
     * Obtiene todos los platos
     */
    List<DishResponse> findAll();
    
    /**
     * Obtiene un plato por su ID
     */
    DishResponse findById(Long id);
    
    /**
     * Obtiene todos los platos de una categoría específica
     */
    List<DishResponse> findByCategoryId(Long categoryId);
    
    /**
     * Crea un nuevo plato
     */
    DishResponse create(DishRequest request);
    
    /**
     * Actualiza un plato existente
     */
    DishResponse update(Long id, DishRequest request);
    
    /**
     * Elimina un plato
     */
    void delete(Long id);
}
