package com.elpolloempoderado.backend.controller;

import com.elpolloempoderado.backend.dto.AddressRequest;
import com.elpolloempoderado.backend.dto.AddressResponse;
import com.elpolloempoderado.backend.service.AddressService;
import com.elpolloempoderado.backend.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de direcciones de envío
 */
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {
    
    private final AddressService addressService;
    
    /**
     * Obtiene todas las direcciones del usuario autenticado
     * GET /api/addresses
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<AddressResponse>> getMyAddresses() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<AddressResponse> addresses = addressService.getUserAddresses(userId);
        return ResponseEntity.ok(addresses);
    }
    
    /**
     * Obtiene una dirección específica del usuario
     * GET /api/addresses/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AddressResponse> getAddressById(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        AddressResponse address = addressService.getAddressById(id, userId);
        return ResponseEntity.ok(address);
    }
    
    /**
     * Obtiene la dirección por defecto del usuario
     * GET /api/addresses/default
     */
    @GetMapping("/default")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AddressResponse> getDefaultAddress() {
        Long userId = SecurityUtil.getCurrentUserId();
        AddressResponse address = addressService.getDefaultAddress(userId);
        return ResponseEntity.ok(address);
    }
    
    /**
     * Crea una nueva dirección para el usuario
     * POST /api/addresses
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AddressResponse> createAddress(@Valid @RequestBody AddressRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        AddressResponse address = addressService.createAddress(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(address);
    }
    
    /**
     * Actualiza una dirección existente
     * PUT /api/addresses/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        AddressResponse address = addressService.updateAddress(id, request, userId);
        return ResponseEntity.ok(address);
    }
    
    /**
     * Establece una dirección como predeterminada
     * PATCH /api/addresses/{id}/set-default
     */
    @PatchMapping("/{id}/set-default")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AddressResponse> setDefaultAddress(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        AddressResponse address = addressService.setDefaultAddress(id, userId);
        return ResponseEntity.ok(address);
    }
    
    /**
     * Elimina una dirección
     * DELETE /api/addresses/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        addressService.deleteAddress(id, userId);
        return ResponseEntity.noContent().build();
    }
}
