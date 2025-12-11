package com.elpolloempoderado.backend.service;

import com.elpolloempoderado.backend.dto.*;
import com.elpolloempoderado.backend.exception.BadRequestException;
import com.elpolloempoderado.backend.exception.ForbiddenException;
import com.elpolloempoderado.backend.exception.ResourceNotFoundException;
import com.elpolloempoderado.backend.model.*;
import com.elpolloempoderado.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de pedidos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final DishRepository dishRepository;
    private final EmailService emailService;
    
    // Costo de envío fijo (puede configurarse desde properties)
    private static final BigDecimal DELIVERY_FEE = new BigDecimal("5.00");
    
    /**
     * Crea un nuevo pedido (checkout)
     */
    @Transactional
    public OrderResponse createOrder(CheckoutRequest request, Long userId) {
        log.info("Creando pedido para usuario: {}", userId);
        
        // Validar usuario
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        // Validar dirección
        Address address = addressRepository.findById(request.getAddressId())
            .orElseThrow(() -> new ResourceNotFoundException("Dirección no encontrada"));
        
        if (!address.getUser().getId().equals(userId)) {
            throw new ForbiddenException("La dirección no pertenece al usuario");
        }
        
        // Validar que el carrito no esté vacío
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("El carrito está vacío");
        }
        
        // Crear el pedido
        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setStatus(OrderStatus.PENDING);
        order.setNotes(request.getNotes());
        order.setReceiptEmail(user.getEmail());
        order.setDeliveryFee(DELIVERY_FEE);
        
        // Calcular tiempo estimado de entrega (45 minutos desde ahora)
        order.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(45));
        
        // Crear los items del pedido
        for (CartItemRequest itemRequest : request.getItems()) {
            Dish dish = dishRepository.findById(itemRequest.getDishId())
                .orElseThrow(() -> new ResourceNotFoundException("Plato no encontrado: " + itemRequest.getDishId()));
            
            OrderItem orderItem = new OrderItem();
            orderItem.setDish(dish);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(dish.getPrice());
            orderItem.calculateSubtotal();
            
            order.addItem(orderItem);
        }
        
        // Calcular el total
        order.calculateTotal();
        
        // Guardar el pedido
        Order savedOrder = orderRepository.save(order);
        
        log.info("Pedido creado exitosamente: {}", savedOrder.getOrderNumber());
        
        // Enviar correo de confirmación (asíncrono)
        try {
            emailService.sendOrderConfirmation(savedOrder);
        } catch (Exception e) {
            log.error("Error al enviar correo de confirmación", e);
            // No fallar el pedido si falla el email
        }
        
        return convertToResponse(savedOrder);
    }
    
    /**
     * Obtiene todos los pedidos del usuario
     */
    public List<OrderResponse> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene pedidos del usuario con paginación
     */
    public Page<OrderResponse> getUserOrdersPaginated(Long userId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);
        return orders.map(this::convertToResponse);
    }
    
    /**
     * Obtiene un pedido específico del usuario
     */
    public OrderResponse getOrderById(Long orderId, Long userId) {
        Order order = getOrderOrThrow(orderId, userId);
        return convertToResponse(order);
    }
    
    /**
     * Obtiene un pedido por número de orden
     */
    public OrderResponse getOrderByNumber(String orderNumber, Long userId) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        
        if (!order.getUser().getId().equals(userId)) {
            throw new ForbiddenException("No tienes permiso para ver este pedido");
        }
        
        return convertToResponse(order);
    }
    
    /**
     * Obtiene pedidos del usuario por estado
     */
    public List<OrderResponse> getUserOrdersByStatus(Long userId, OrderStatus status) {
        List<Order> orders = orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);
        return orders.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Cancela un pedido (solo si está en estado PENDING o CONFIRMED)
     */
    @Transactional
    public OrderResponse cancelOrder(Long orderId, Long userId) {
        Order order = getOrderOrThrow(orderId, userId);
        
        // Validar que el pedido se puede cancelar
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new BadRequestException("No se puede cancelar el pedido en su estado actual");
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        Order updatedOrder = orderRepository.save(order);
        
        log.info("Pedido cancelado: {}", order.getOrderNumber());
        
        return convertToResponse(updatedOrder);
    }
    
    /**
     * Actualiza el estado de un pedido (solo para admin)
     */
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        
        order.setStatus(newStatus);
        
        // Si se marca como entregado, registrar la fecha
        if (newStatus == OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
        }
        
        Order updatedOrder = orderRepository.save(order);
        
        log.info("Estado del pedido actualizado: {} -> {}", order.getOrderNumber(), newStatus);
        
        return convertToResponse(updatedOrder);
    }
    
    /**
     * Actualiza el estado del pago (usado por webhooks de MercadoPago)
     */
    @Transactional
    public OrderResponse updatePaymentStatus(Long orderId, PaymentStatus paymentStatus, String transactionId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        
        order.setPaymentStatus(paymentStatus);
        order.setPaymentTransactionId(transactionId);
        
        // Si el pago fue aprobado, confirmar el pedido
        if (paymentStatus == PaymentStatus.APPROVED && order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.CONFIRMED);
        }
        
        Order updatedOrder = orderRepository.save(order);
        
        log.info("Estado de pago actualizado: {} -> {}", order.getOrderNumber(), paymentStatus);
        
        return convertToResponse(updatedOrder);
    }
    
    /**
     * Obtiene estadísticas de pedidos del usuario
     */
    public OrderStatistics getUserOrderStatistics(Long userId) {
        long totalOrders = orderRepository.countByUserId(userId);
        long pendingOrders = orderRepository.countByUserIdAndStatus(userId, OrderStatus.PENDING);
        long completedOrders = orderRepository.countByUserIdAndStatus(userId, OrderStatus.DELIVERED);
        long cancelledOrders = orderRepository.countByUserIdAndStatus(userId, OrderStatus.CANCELLED);
        
        return new OrderStatistics(totalOrders, pendingOrders, completedOrders, cancelledOrders);
    }
    
    // ============= MÉTODOS PRIVADOS =============
    
    private Order getOrderOrThrow(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        
        if (!order.getUser().getId().equals(userId)) {
            throw new ForbiddenException("No tienes permiso para acceder a este pedido");
        }
        
        return order;
    }
    
    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setUserId(order.getUser().getId());
        response.setUserEmail(order.getUser().getEmail());
        
        // Address
        response.setAddress(convertAddressToResponse(order.getAddress()));
        
        // Items
        List<OrderItemResponse> items = order.getItems().stream()
            .map(this::convertItemToResponse)
            .collect(Collectors.toList());
        response.setItems(items);
        
        response.setSubtotal(order.getSubtotal());
        response.setDeliveryFee(order.getDeliveryFee());
        response.setTotal(order.getTotal());
        response.setStatus(order.getStatus());
        response.setStatusDisplayName(order.getStatus().getDisplayName());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setPaymentMethodDisplayName(order.getPaymentMethod().getDisplayName());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setPaymentStatusDisplayName(order.getPaymentStatus().getDisplayName());
        response.setPaymentTransactionId(order.getPaymentTransactionId());
        response.setNotes(order.getNotes());
        response.setReceiptEmail(order.getReceiptEmail());
        response.setEstimatedDeliveryTime(order.getEstimatedDeliveryTime());
        response.setDeliveredAt(order.getDeliveredAt());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        
        return response;
    }
    
    private OrderItemResponse convertItemToResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        response.setDishId(item.getDish().getId());
        response.setDishName(item.getDish().getName());
        response.setDishImageUrl(item.getDish().getImageUrl());
        response.setQuantity(item.getQuantity());
        response.setUnitPrice(item.getUnitPrice());
        response.setSubtotal(item.getSubtotal());
        return response;
    }
    
    private AddressResponse convertAddressToResponse(Address address) {
        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setUserId(address.getUser().getId());
        
        CityResponse cityResponse = new CityResponse();
        cityResponse.setId(address.getCity().getId());
        cityResponse.setNombre(address.getCity().getNombre());
        response.setCity(cityResponse);
        
        DistrictResponse districtResponse = new DistrictResponse();
        districtResponse.setId(address.getDistrict().getId());
        districtResponse.setNombre(address.getDistrict().getNombre());
        districtResponse.setCiudadId(address.getCity().getId());
        response.setDistrict(districtResponse);
        
        response.setStreet(address.getStreet());
        response.setNumber(address.getNumber());
        response.setReference(address.getReference());
        response.setPhone(address.getPhone());
        response.setLabel(address.getLabel());
        response.setIsDefault(address.getIsDefault());
        
        return response;
    }
    
    /**
     * Clase interna para estadísticas de pedidos
     */
    public static class OrderStatistics {
        public final long totalOrders;
        public final long pendingOrders;
        public final long completedOrders;
        public final long cancelledOrders;
        
        public OrderStatistics(long totalOrders, long pendingOrders, long completedOrders, long cancelledOrders) {
            this.totalOrders = totalOrders;
            this.pendingOrders = pendingOrders;
            this.completedOrders = completedOrders;
            this.cancelledOrders = cancelledOrders;
        }
    }
}
