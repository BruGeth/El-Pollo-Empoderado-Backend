package com.elpolloempoderado.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "dish")
@Getter
@Setter
@NoArgsConstructor // requerido por JPA
@AllArgsConstructor
@ToString(exclude = "category") // evitar recursión en toString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Dish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    private String description;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false) // precio debe ser > 0
    @Column(nullable = false)
    private BigDecimal price;

    private String imageUrl;

    // ManyToOne: cada Dish pertenece a una Category.
    // FetchType.LAZY evita cargar la categoría a menos que sea necesario.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonBackReference // contraparte de JsonManagedReference; evita recursión al serializar
    private Category category;

    // Constructor de conveniencia
    public Dish(String name, String description, BigDecimal price, String imageUrl, Category category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
    }
}