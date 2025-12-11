package com.elpolloempoderado.backend.controller;

import com.elpolloempoderado.backend.dto.CityResponse;
import com.elpolloempoderado.backend.dto.DistrictResponse;
import com.elpolloempoderado.backend.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar ciudades y distritos
 * Endpoints públicos para consultar ubicaciones disponibles
 */
@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {
    
    private final LocationService locationService;
    
    /**
     * Obtener todas las ciudades disponibles
     */
    @GetMapping("/cities")
    public ResponseEntity<List<CityResponse>> getAllCities() {
        return ResponseEntity.ok(locationService.getAllCities());
    }
    
    /**
     * Obtener todos los distritos
     */
    @GetMapping("/districts")
    public ResponseEntity<List<DistrictResponse>> getAllDistricts() {
        return ResponseEntity.ok(locationService.getAllDistricts());
    }
    
    /**
     * Obtener distritos de una ciudad específica
     */
    @GetMapping("/cities/{cityId}/districts")
    public ResponseEntity<List<DistrictResponse>> getDistrictsByCity(@PathVariable Long cityId) {
        return ResponseEntity.ok(locationService.getDistrictsByCity(cityId));
    }
}
