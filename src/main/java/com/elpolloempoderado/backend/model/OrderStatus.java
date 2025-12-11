package com.elpolloempoderado.backend.model;

/**
 * Estados del pedido en el ciclo de vida de una orden
 */
public enum OrderStatus {
    PENDING("Pendiente"),           // Pedido creado, esperando confirmación
    CONFIRMED("Confirmado"),        // Pedido confirmado por el sistema
    PREPARING("En preparación"),    // Pedido en cocina
    READY("Listo"),                 // Pedido listo para entrega/recojo
    ON_DELIVERY("En camino"),       // Pedido en ruta de entrega
    DELIVERED("Entregado"),         // Pedido entregado al cliente
    CANCELLED("Cancelado");         // Pedido cancelado

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
