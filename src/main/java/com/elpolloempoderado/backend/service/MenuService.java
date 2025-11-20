package com.elpolloempoderado.backend.service;

import com.elpolloempoderado.backend.dto.MenuCategoryResponse;

import java.util.List;

/**
 * Servicio para gestionar el menú completo con categorías y platos
 */
public interface MenuService {
    
    /**
     * Obtiene el menú completo con todas las categorías y sus platos asociados
     * Optimizado con fetch join para minimizar consultas a la base de datos
     * @return Lista de categorías con sus platos, ordenadas alfabéticamente
     */
    List<MenuCategoryResponse> getFullMenu();
}
