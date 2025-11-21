package com.elpolloempoderado.backend.controller;

import com.elpolloempoderado.backend.dto.MenuCategoryResponse;
import com.elpolloempoderado.backend.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para el endpoint del menú
 * Proporciona un endpoint optimizado para obtener todas las categorías con sus platos
 */
@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {
    
    private final MenuService menuService;
    
    /**
     * GET /api/menu - Obtiene el menú completo con categorías y platos
     * Optimizado con fetch join para minimizar consultas a la base de datos
     * Ideal para la pantalla principal del menú en el frontend
     * 
     * @return Lista de categorías con sus platos asociados, ordenadas alfabéticamente
     */
    @GetMapping
    public ResponseEntity<List<MenuCategoryResponse>> getFullMenu() {
        List<MenuCategoryResponse> menu = menuService.getFullMenu();
        return ResponseEntity.ok(menu);
    }
}
