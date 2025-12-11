package com.elpolloempoderado.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entidad que representa las ciudades disponibles en el sistema
 */
@Entity
@Table(name = "cities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class City {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ciudad_id")
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
}
