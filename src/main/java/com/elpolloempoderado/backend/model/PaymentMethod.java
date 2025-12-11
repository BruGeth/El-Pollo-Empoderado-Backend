package com.elpolloempoderado.backend.model;

/**
 * Métodos de pago disponibles
 */
public enum PaymentMethod {
    MERCADO_PAGO("MercadoPago"),
    CASH("Efectivo"),
    CARD("Tarjeta");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
