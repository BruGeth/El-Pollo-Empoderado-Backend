package com.elpolloempoderado.backend.controller;

import com.elpolloempoderado.backend.dto.PaymentResponse;
import com.elpolloempoderado.backend.service.MercadoPagoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para gestionar pagos con MercadoPago
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final MercadoPagoService mercadoPagoService;

    /**
     * Crear preferencia de pago para una orden
     * POST /api/payments/create-preference?orderId=1
     */
    @PostMapping("/create-preference")
    public ResponseEntity<PaymentResponse> createPaymentPreference(@RequestParam Long orderId) {
        log.info("Creando preferencia de pago para orden: {}", orderId);
        PaymentResponse response = mercadoPagoService.createPaymentPreference(orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener Public Key de MercadoPago para el frontend
     * GET /api/payments/public-key
     */
    @GetMapping("/public-key")
    public ResponseEntity<Map<String, String>> getPublicKey() {
        String publicKey = mercadoPagoService.getPublicKey();
        return ResponseEntity.ok(Map.of("publicKey", publicKey));
    }

    /**
     * Webhook para recibir notificaciones de MercadoPago
     * POST /api/payments/webhook?type=payment&data.id=123456
     */
    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "data.id", required = false) String dataId,
            @RequestBody(required = false) Map<String, Object> payload) {
        
        log.info("Webhook recibido - Type: {}, Data.ID: {}", type, dataId);
        log.debug("Payload completo: {}", payload);

        try {
            // MercadoPago envía el ID del pago en el parámetro data.id
            String paymentId = dataId;
            
            // Si no viene en el parámetro, intentar extraerlo del body
            if (paymentId == null && payload != null && payload.containsKey("data")) {
                Object data = payload.get("data");
                if (data instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> dataMap = (Map<String, Object>) data;
                    Object id = dataMap.get("id");
                    if (id != null) {
                        paymentId = id.toString();
                    }
                }
            }

            if (paymentId != null) {
                mercadoPagoService.processWebhookNotification(type, paymentId);
                log.info("Webhook procesado exitosamente para payment ID: {}", paymentId);
            } else {
                log.warn("No se pudo obtener el payment ID del webhook");
            }

            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            log.error("Error al procesar webhook de MercadoPago", e);
            // Retornar 200 de todas formas para evitar reintentos de MercadoPago
            return ResponseEntity.ok().build();
        }
    }

    /**
     * Endpoint de prueba para verificar configuración
     * GET /api/payments/test
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testConfiguration() {
        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "message", "MercadoPago configurado correctamente",
                "publicKey", mercadoPagoService.getPublicKey()
        ));
    }
}
