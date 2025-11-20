package com.elpolloempoderado.backend.repository;

import com.elpolloempoderado.backend.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {
    
    /**
     * Encuentra todos los platos de una categoría específica
     */
    List<Dish> findByCategoryId(Long categoryId);
    
    /**
     * Verifica si existe un plato con el nombre dado
     */
    boolean existsByName(String name);
}
