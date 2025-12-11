package com.elpolloempoderado.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta con información del pago de MercadoPago
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String preferenceId;      // ID de la preferencia de MercadoPago
    private String initPoint;         // URL para redirección al checkout
    private String sandboxInitPoint;  // URL de sandbox para testing
    private Long orderId;             // ID de la orden asociada
    private String status;            // Estado del pago
}
