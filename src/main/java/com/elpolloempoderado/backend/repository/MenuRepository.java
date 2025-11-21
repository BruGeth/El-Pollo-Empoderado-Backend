package com.elpolloempoderado.backend.repository;

import com.elpolloempoderado.backend.model.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio especializado para consultas del menú
 * Optimizado para obtener categorías con sus platos en una sola consulta
 */
@Repository
@RequiredArgsConstructor
public class MenuRepository {
    
    @PersistenceContext
    private final EntityManager entityManager;
    
    /**
     * Obtiene todas las categorías con sus platos usando fetch join para optimizar consultas
     * DISTINCT evita duplicados por la relación OneToMany
     * Ordenamiento dual: categorías por nombre, platos por nombre
     * 
     * @return Lista de categorías con platos cargados
     */
    public List<Category> findAllCategoriesWithDishes() {
        String jpql = "SELECT DISTINCT c FROM Category c " +
                     "LEFT JOIN FETCH c.dishes d " +
                     "ORDER BY c.name ASC, d.name ASC";
        
        TypedQuery<Category> query = entityManager.createQuery(jpql, Category.class);
        return query.getResultList();
    }
}
