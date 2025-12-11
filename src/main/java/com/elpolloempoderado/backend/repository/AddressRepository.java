package com.elpolloempoderado.backend.repository;

import com.elpolloempoderado.backend.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar direcciones de usuarios
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    /**
     * Encuentra todas las direcciones de un usuario
     */
    List<Address> findByUserId(Long userId);
    
    /**
     * Encuentra todas las direcciones de un usuario ordenadas por isDefault descendente
     */
    List<Address> findByUserIdOrderByIsDefaultDesc(Long userId);
    
    /**
     * Encuentra la dirección por defecto de un usuario
     */
    Optional<Address> findByUserIdAndIsDefaultTrue(Long userId);
    
    /**
     * Encuentra una dirección específica de un usuario
     */
    Optional<Address> findByIdAndUserId(Long id, Long userId);
    
    /**
     * Cuenta cuántas direcciones tiene un usuario
     */
    long countByUserId(Long userId);
}
