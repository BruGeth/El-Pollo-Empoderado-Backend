package com.elpolloempoderado.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para solicitud de pago con MercadoPago
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private Long orderId;           // ID de la orden
    private BigDecimal amount;      // Monto total
    private String description;     // Descripción del pago
    private String payerEmail;      // Email del pagador
}
