package com.elpolloempoderado.backend.controller;

import com.elpolloempoderado.backend.dto.LocalRequest;
import com.elpolloempoderado.backend.dto.LocalResponse;
import com.elpolloempoderado.backend.service.LocalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar locales/tiendas
 * GET endpoints son públicos, CUD requieren rol ADMIN
 */
@RestController
@RequestMapping("/api/locals")
@RequiredArgsConstructor
public class LocalController {
    
    private final LocalService localService;
    
    /**
     * Obtener todos los locales
     */
    @GetMapping
    public ResponseEntity<List<LocalResponse>> getAllLocals() {
        return ResponseEntity.ok(localService.getAllLocals());
    }
    
    /**
     * Obtener un local por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<LocalResponse> getLocalById(@PathVariable Long id) {
        return ResponseEntity.ok(localService.getLocalById(id));
    }
    
    /**
     * Obtener locales por distrito
     */
    @GetMapping("/district/{districtId}")
    public ResponseEntity<List<LocalResponse>> getLocalsByDistrict(@PathVariable Long districtId) {
        return ResponseEntity.ok(localService.getLocalsByDistrict(districtId));
    }
    
    /**
     * Obtener locales por ciudad
     */
    @GetMapping("/city/{cityId}")
    public ResponseEntity<List<LocalResponse>> getLocalsByCity(@PathVariable Long cityId) {
        return ResponseEntity.ok(localService.getLocalsByCity(cityId));
    }
    
    /**
     * Crear un nuevo local (solo ADMIN)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LocalResponse> createLocal(@RequestBody LocalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(localService.createLocal(request));
    }
    
    /**
     * Actualizar un local existente (solo ADMIN)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LocalResponse> updateLocal(@PathVariable Long id, @RequestBody LocalRequest request) {
        return ResponseEntity.ok(localService.updateLocal(id, request));
    }
    
    /**
     * Eliminar un local (solo ADMIN)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLocal(@PathVariable Long id) {
        localService.deleteLocal(id);
        return ResponseEntity.noContent().build();
    }
}
