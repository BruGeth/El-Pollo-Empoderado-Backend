package com.elpolloempoderado.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa una dirección de envío del usuario
 * Un usuario puede tener múltiples direcciones
 */
@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Relación con el usuario propietario de la dirección
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Ciudad - referencia a la tabla cities con primary key ciudad_id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ciudad_id", nullable = false, referencedColumnName = "ciudad_id")
    private City city;
    
    // Distrito - referencia a la tabla districts con primary key distrito_id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "distrito_id", nullable = false, referencedColumnName = "distrito_id")
    private District district;
    
    // Dirección exacta (calle, avenida, jirón)
    @Column(nullable = false, length = 255)
    private String street;
    
    // Número de casa/departamento (opcional)
    @Column(length = 50)
    private String number;
    
    // Referencia de la ubicación (misma que reference_home en users)
    @Column(name = "reference_home", length = 255)
    private String reference;
    
    // Teléfono de contacto para esta dirección (mismo que telefono en users)
    @Column(name = "telefono", nullable = false, length = 20)
    private String phone;
    
    // Etiqueta de la dirección (Casa, Trabajo, etc.)
    @Column(length = 50)
    private String label;
    
    // Indica si es la dirección por defecto
    @Column(nullable = false)
    private Boolean isDefault = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
