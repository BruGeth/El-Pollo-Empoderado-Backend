package com.elpolloempoderado.backend.dto;

import com.elpolloempoderado.backend.model.OrderStatus;
import com.elpolloempoderado.backend.model.PaymentMethod;
import com.elpolloempoderado.backend.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para retornar información de un pedido
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    
    private Long id;
    private String orderNumber;
    private Long userId;
    private String userEmail;
    private AddressResponse address;
    private List<OrderItemResponse> items;
    private BigDecimal subtotal;
    private BigDecimal deliveryFee;
    private BigDecimal total;
    private OrderStatus status;
    private String statusDisplayName;
    private PaymentMethod paymentMethod;
    private String paymentMethodDisplayName;
    private PaymentStatus paymentStatus;
    private String paymentStatusDisplayName;
    private String paymentTransactionId;
    private String notes;
    private String receiptEmail;
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime deliveredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
