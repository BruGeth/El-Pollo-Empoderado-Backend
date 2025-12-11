package com.elpolloempoderado.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entidad que representa los distritos disponibles en el sistema
 * Cada distrito pertenece a una ciudad
 */
@Entity
@Table(name = "districts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class District {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "distrito_id")
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @ManyToOne
    @JoinColumn(name = "ciudad_id", nullable = false)
    private City city;
}
