package com.elpolloempoderado.backend.controller;

import com.elpolloempoderado.backend.dto.DishRequest;
import com.elpolloempoderado.backend.dto.DishResponse;
import com.elpolloempoderado.backend.service.DishService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dishes")
@RequiredArgsConstructor
public class DishController {
    
    private final DishService dishService;
    
    /**
     * GET /api/dishes - Obtiene todos los platos
     */
    @GetMapping
    public ResponseEntity<List<DishResponse>> getAllDishes() {
        List<DishResponse> dishes = dishService.findAll();
        return ResponseEntity.ok(dishes);
    }
    
    /**
     * GET /api/dishes/{id} - Obtiene un plato por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<DishResponse> getDishById(@PathVariable Long id) {
        DishResponse dish = dishService.findById(id);
        return ResponseEntity.ok(dish);
    }
    
    /**
     * GET /api/dishes/category/{categoryId} - Obtiene platos por categoría
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<DishResponse>> getDishesByCategory(@PathVariable Long categoryId) {
        List<DishResponse> dishes = dishService.findByCategoryId(categoryId);
        return ResponseEntity.ok(dishes);
    }
    
    /**
     * POST /api/dishes - Crea un nuevo plato (solo ADMIN)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DishResponse> createDish(@Valid @RequestBody DishRequest request) {
        DishResponse dish = dishService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dish);
    }
    
    /**
     * PUT /api/dishes/{id} - Actualiza un plato existente (solo ADMIN)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DishResponse> updateDish(@PathVariable Long id, @Valid @RequestBody DishRequest request) {
        DishResponse dish = dishService.update(id, request);
        return ResponseEntity.ok(dish);
    }
    
    /**
     * DELETE /api/dishes/{id} - Elimina un plato (solo ADMIN)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDish(@PathVariable Long id) {
        dishService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
