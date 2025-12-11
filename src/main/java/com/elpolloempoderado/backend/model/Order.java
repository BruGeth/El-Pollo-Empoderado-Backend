package com.elpolloempoderado.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un pedido/orden del sistema
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Número de orden único para mostrar al cliente
    @Column(nullable = false, unique = true, length = 50)
    private String orderNumber;
    
    // Usuario que realizó el pedido
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Dirección de envío del pedido
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;
    
    // Items del pedido
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
    
    // Total del pedido
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    // Costo de envío
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal deliveryFee = BigDecimal.ZERO;
    
    // Total final (subtotal + delivery)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    // Estado del pedido
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;
    
    // Método de pago
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod paymentMethod;
    
    // Estado del pago
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    // ID de transacción de MercadoPago (si aplica)
    @Column(length = 100)
    private String paymentTransactionId;
    
    // Notas adicionales del cliente
    @Column(length = 500)
    private String notes;
    
    // Email donde se envió la boleta
    @Column(length = 150)
    private String receiptEmail;
    
    // Fecha de entrega estimada
    @Column(name = "estimated_delivery_time")
    private LocalDateTime estimatedDeliveryTime;
    
    // Fecha de entrega real
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        
        // Generar número de orden único
        if (orderNumber == null) {
            orderNumber = generateOrderNumber();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Genera un número de orden único basado en timestamp
     */
    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }
    
    /**
     * Calcula el total del pedido
     */
    public void calculateTotal() {
        this.subtotal = items.stream()
            .map(OrderItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.total = this.subtotal.add(this.deliveryFee);
    }
    
    /**
     * Agrega un item al pedido
     */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
    
    /**
     * Remueve un item del pedido
     */
    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }
}
