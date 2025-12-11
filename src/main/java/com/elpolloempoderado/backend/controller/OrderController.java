package com.elpolloempoderado.backend.controller;

import com.elpolloempoderado.backend.dto.CheckoutRequest;
import com.elpolloempoderado.backend.dto.OrderResponse;
import com.elpolloempoderado.backend.model.OrderStatus;
import com.elpolloempoderado.backend.model.PaymentStatus;
import com.elpolloempoderado.backend.service.OrderService;
import com.elpolloempoderado.backend.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de pedidos
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    /**
     * Crea un nuevo pedido (checkout)
     * POST /api/orders/checkout
     */
    @PostMapping("/checkout")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CheckoutRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        OrderResponse order = orderService.createOrder(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
    
    /**
     * Obtiene todos los pedidos del usuario autenticado
     * GET /api/orders
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<OrderResponse> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }
    
    /**
     * Obtiene pedidos del usuario con paginación
     * GET /api/orders/paginated?page=0&size=10
     */
    @GetMapping("/paginated")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<OrderResponse>> getMyOrdersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtil.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderResponse> orders = orderService.getUserOrdersPaginated(userId, pageable);
        return ResponseEntity.ok(orders);
    }
    
    /**
     * Obtiene un pedido específico del usuario
     * GET /api/orders/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        OrderResponse order = orderService.getOrderById(id, userId);
        return ResponseEntity.ok(order);
    }
    
    /**
     * Obtiene un pedido por número de orden
     * GET /api/orders/number/{orderNumber}
     */
    @GetMapping("/number/{orderNumber}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OrderResponse> getOrderByNumber(@PathVariable String orderNumber) {
        Long userId = SecurityUtil.getCurrentUserId();
        OrderResponse order = orderService.getOrderByNumber(orderNumber, userId);
        return ResponseEntity.ok(order);
    }
    
    /**
     * Obtiene pedidos del usuario filtrados por estado
     * GET /api/orders/status/{status}
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable OrderStatus status) {
        Long userId = SecurityUtil.getCurrentUserId();
        List<OrderResponse> orders = orderService.getUserOrdersByStatus(userId, status);
        return ResponseEntity.ok(orders);
    }
    
    /**
     * Cancela un pedido
     * PATCH /api/orders/{id}/cancel
     */
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        OrderResponse order = orderService.cancelOrder(id, userId);
        return ResponseEntity.ok(order);
    }
    
    /**
     * Obtiene estadísticas de pedidos del usuario
     * GET /api/orders/statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OrderService.OrderStatistics> getOrderStatistics() {
        Long userId = SecurityUtil.getCurrentUserId();
        OrderService.OrderStatistics stats = orderService.getUserOrderStatistics(userId);
        return ResponseEntity.ok(stats);
    }
    
    // ============= ENDPOINTS PARA ADMIN =============
    
    /**
     * Actualiza el estado de un pedido (solo admin)
     * PATCH /api/orders/{id}/status
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        OrderStatus status = OrderStatus.valueOf(request.get("status"));
        OrderResponse order = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(order);
    }
    
    /**
     * Actualiza el estado del pago (usado por webhooks de MercadoPago)
     * PATCH /api/orders/{id}/payment-status
     */
    @PatchMapping("/{id}/payment-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updatePaymentStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        PaymentStatus paymentStatus = PaymentStatus.valueOf(request.get("paymentStatus"));
        String transactionId = request.get("transactionId");
        OrderResponse order = orderService.updatePaymentStatus(id, paymentStatus, transactionId);
        return ResponseEntity.ok(order);
    }
}
