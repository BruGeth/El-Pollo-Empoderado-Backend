package com.elpolloempoderado.backend.model;

/**
 * Estados del pago del pedido
 */
public enum PaymentStatus {
    PENDING("Pendiente"),           // Pago pendiente
    PROCESSING("Procesando"),       // Pago en proceso (MercadoPago)
    APPROVED("Aprobado"),           // Pago aprobado
    REJECTED("Rechazado"),          // Pago rechazado
    REFUNDED("Reembolsado");        // Pago reembolsado

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
