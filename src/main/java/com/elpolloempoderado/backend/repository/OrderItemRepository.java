package com.elpolloempoderado.backend.repository;

import com.elpolloempoderado.backend.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para gestionar items de pedidos
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    /**
     * Encuentra todos los items de un pedido
     */
    List<OrderItem> findByOrderId(Long orderId);
    
    /**
     * Encuentra items por plato específico
     */
    List<OrderItem> findByDishId(Long dishId);
    
    /**
     * Obtiene los platos más vendidos
     */
    @Query("SELECT oi.dish.id, SUM(oi.quantity) as total FROM OrderItem oi " +
           "GROUP BY oi.dish.id ORDER BY total DESC")
    List<Object[]> findBestSellingDishes();
    
    /**
     * Obtiene el total de unidades vendidas de un plato
     */
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.dish.id = :dishId")
    Long getTotalQuantitySoldByDish(@Param("dishId") Long dishId);
}
