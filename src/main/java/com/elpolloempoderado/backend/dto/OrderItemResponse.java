package com.elpolloempoderado.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para retornar información de un item del pedido
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    
    private Long id;
    private Long dishId;
    private String dishName;
    private String dishImageUrl;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
