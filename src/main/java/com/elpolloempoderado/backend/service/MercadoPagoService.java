package com.elpolloempoderado.backend.service;

import com.elpolloempoderado.backend.dto.PaymentResponse;
import com.elpolloempoderado.backend.model.Order;
import com.elpolloempoderado.backend.model.OrderItem;
import com.elpolloempoderado.backend.model.OrderStatus;
import com.elpolloempoderado.backend.model.PaymentStatus;
import com.elpolloempoderado.backend.repository.OrderRepository;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para gestionar pagos con MercadoPago
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MercadoPagoService {

    private final OrderRepository orderRepository;
    private final EmailService emailService;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Value("${mercadopago.public-key}")
    private String publicKey;

    @Value("${app.base-url:http://localhost:4200}")
    private String frontendBaseUrl;

    /**
     * Crear preferencia de pago en MercadoPago
     */
    public PaymentResponse createPaymentPreference(Long orderId) {
        try {
            // Configurar el Access Token
            MercadoPagoConfig.setAccessToken(accessToken);

            // Buscar la orden
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

            // Crear items de la preferencia
            List<PreferenceItemRequest> items = new ArrayList<>();
            for (OrderItem item : order.getItems()) {
                PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                        .id(item.getDish().getId().toString())
                        .title(item.getDish().getName())
                        .description(item.getDish().getDescription())
                        .quantity(item.getQuantity())
                        .currencyId("PEN") // Soles peruanos
                        .unitPrice(item.getUnitPrice())
                        .build();
                items.add(itemRequest);
            }

            // URLs de retorno
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(frontendBaseUrl + "/payment/success")
                    .failure(frontendBaseUrl + "/payment/failure")
                    .pending(frontendBaseUrl + "/payment/pending")
                    .build();

            // Crear la preferencia
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls)
                    .autoReturn("approved") // Retorno automático al aprobar
                    .externalReference(order.getOrderNumber()) // Número de orden como referencia
                    .notificationUrl(frontendBaseUrl + "/api/payments/webhook") // Webhook para notificaciones
                    .payer(com.mercadopago.client.preference.PreferencePayerRequest.builder()
                            .email(order.getUser().getEmail())
                            .name(order.getUser().getFirstName())
                            .surname(order.getUser().getLastName())
                            .build())
                    .build();

            // Crear la preferencia en MercadoPago
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            // Construir respuesta
            PaymentResponse response = new PaymentResponse();
            response.setPreferenceId(preference.getId());
            response.setInitPoint(preference.getInitPoint());
            response.setSandboxInitPoint(preference.getSandboxInitPoint());
            response.setOrderId(orderId);
            response.setStatus("created");

            log.info("Preferencia de pago creada: {} para orden: {}", preference.getId(), order.getOrderNumber());

            return response;

        } catch (MPException | MPApiException e) {
            log.error("Error al crear preferencia de pago en MercadoPago", e);
            throw new RuntimeException("Error al procesar el pago: " + e.getMessage());
        }
    }

    /**
     * Obtener el Public Key para el frontend
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * Procesar webhook de MercadoPago
     * @param type Tipo de notificación (payment, merchant_order, etc.)
     * @param paymentId ID del pago en MercadoPago
     */
    public void processWebhookNotification(String type, String paymentId) {
        try {
            log.info("Procesando webhook - Type: {}, PaymentId: {}", type, paymentId);

            // Solo procesar notificaciones de tipo "payment"
            if (!"payment".equals(type)) {
                log.info("Tipo de notificación ignorado: {}", type);
                return;
            }

            // Configurar el Access Token
            MercadoPagoConfig.setAccessToken(accessToken);

            // Obtener información del pago desde MercadoPago
            PaymentClient paymentClient = new PaymentClient();
            Payment payment = paymentClient.get(Long.parseLong(paymentId));

            log.info("Pago obtenido - Status: {}, ExternalReference: {}", 
                    payment.getStatus(), payment.getExternalReference());

            // Buscar la orden por external reference (orderNumber)
            String orderNumber = payment.getExternalReference();
            Order order = orderRepository.findByOrderNumber(orderNumber)
                    .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + orderNumber));

            // Actualizar el transaction ID
            order.setPaymentTransactionId(payment.getId().toString());

            // Actualizar estado según el status de MercadoPago
            switch (payment.getStatus()) {
                case "approved":
                    order.setPaymentStatus(PaymentStatus.APPROVED);
                    order.setStatus(OrderStatus.CONFIRMED);
                    log.info("Pago APROBADO para orden: {}", orderNumber);
                    
                    // Enviar email de confirmación de pago
                    emailService.sendOrderStatusUpdate(order);
                    break;

                case "pending":
                case "in_process":
                    order.setPaymentStatus(PaymentStatus.PROCESSING);
                    log.info("Pago EN PROCESO para orden: {}", orderNumber);
                    break;

                case "rejected":
                case "cancelled":
                    order.setPaymentStatus(PaymentStatus.REJECTED);
                    order.setStatus(OrderStatus.CANCELLED);
                    log.info("Pago RECHAZADO/CANCELADO para orden: {}", orderNumber);
                    
                    // Enviar email de rechazo
                    emailService.sendOrderStatusUpdate(order);
                    break;

                case "refunded":
                    order.setPaymentStatus(PaymentStatus.REFUNDED);
                    order.setStatus(OrderStatus.CANCELLED);
                    log.info("Pago REEMBOLSADO para orden: {}", orderNumber);
                    
                    emailService.sendOrderStatusUpdate(order);
                    break;

                default:
                    log.warn("Estado de pago desconocido: {}", payment.getStatus());
            }

            // Guardar cambios
            orderRepository.save(order);
            log.info("Orden actualizada exitosamente: {}", orderNumber);

        } catch (MPException | MPApiException e) {
            log.error("Error al procesar webhook de MercadoPago", e);
            throw new RuntimeException("Error al procesar webhook: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error general al procesar webhook", e);
            throw new RuntimeException("Error al procesar webhook: " + e.getMessage());
        }
    }
}
