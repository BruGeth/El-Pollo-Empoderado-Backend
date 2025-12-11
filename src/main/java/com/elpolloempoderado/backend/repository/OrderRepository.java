package com.elpolloempoderado.backend.repository;

import com.elpolloempoderado.backend.model.Order;
import com.elpolloempoderado.backend.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar pedidos
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Encuentra un pedido por su número de orden
     */
    Optional<Order> findByOrderNumber(String orderNumber);
    
    /**
     * Encuentra todos los pedidos de un usuario ordenados por fecha de creación descendente
     */
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Encuentra todos los pedidos de un usuario con paginación
     */
    Page<Order> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Encuentra pedidos de un usuario por estado
     */
    List<Order> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, OrderStatus status);
    
    /**
     * Encuentra un pedido específico de un usuario
     */
    Optional<Order> findByIdAndUserId(Long id, Long userId);
    
    /**
     * Encuentra pedidos por estado
     */
    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);
    
    /**
     * Encuentra pedidos creados en un rango de fechas
     */
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Cuenta pedidos de un usuario
     */
    long countByUserId(Long userId);
    
    /**
     * Cuenta pedidos de un usuario por estado
     */
    long countByUserIdAndStatus(Long userId, OrderStatus status);
    
    /**
     * Busca pedidos con items específicos (para análisis)
     */
    @Query("SELECT o FROM Order o JOIN o.items oi WHERE oi.dish.id = :dishId")
    List<Order> findOrdersContainingDish(@Param("dishId") Long dishId);
}
