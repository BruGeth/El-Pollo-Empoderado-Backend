package com.elpolloempoderado.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "category", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Getter
@Setter
@NoArgsConstructor // constructor sin argumentos (requerido por JPA)
@AllArgsConstructor // constructor con todos los campos (generado por Lombok)
@ToString(exclude = "dishes") // evitar incluir lista de dishes en toString (podría causar recursión o salida muy grande)
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // control fino de equals/hashCode
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include // incluir id en equals/hashCode
    private Long id;

    @NotBlank // valida que no sea null ni vacío antes de persistir (útil en futuros controladores)
    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    // Relación one-to-many: una categoría tiene muchas dishes.
    // mappedBy indica que la columna FK se mantiene en la entidad Dish.
    // cascade + orphanRemoval facilitan operaciones en cascada y eliminación de huérfanos.
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // evita recursión infinita al serializar a JSON (la otra parte usa JsonBackReference)
    private List<Dish> dishes = new ArrayList<>();

    // Constructor de conveniencia (no incluye id ni lista)
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // helpers de conveniencia para mantener ambas caras de la relación sincronizadas.
    // Útiles al modificar relaciones en servicios antes de guardar.
    public void addDish(Dish dish) {
        dishes.add(dish);
        dish.setCategory(this);
    }

    public void removeDish(Dish dish) {
        dishes.remove(dish);
        dish.setCategory(null);
    }
}