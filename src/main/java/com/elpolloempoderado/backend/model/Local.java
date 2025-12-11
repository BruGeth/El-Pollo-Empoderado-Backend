package com.elpolloempoderado.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa los locales/tiendas del restaurante
 */
@Entity
@Table(name = "locals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Local {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String nombre;
    
    @ManyToOne
    @JoinColumn(name = "distrito_id", nullable = false)
    private District district;
    
    @Column(nullable = false, length = 255)
    private String direccion;
    
    @Column(length = 20)
    private String telefono;
    
    @Column(length = 100)
    private String horario;
    
    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;
    
    @Column(name = "maps_url", length = 500)
    private String mapsUrl;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
