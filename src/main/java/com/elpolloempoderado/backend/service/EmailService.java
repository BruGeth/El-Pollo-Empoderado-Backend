package com.elpolloempoderado.backend.service;

import com.elpolloempoderado.backend.model.Order;
import com.elpolloempoderado.backend.model.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * Servicio para envío de correos electrónicos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.username:noreply@elpolloempoderado.com}")
    private String fromEmail;
    
    @Value("${app.name:El Pollo Empoderado}")
    private String appName;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Envía el correo de confirmación de pedido (boleta)
     */
    @Async
    public void sendOrderConfirmation(Order order) {
        try {
            log.info("Enviando correo de confirmación para pedido: {}", order.getOrderNumber());
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(order.getReceiptEmail());
            helper.setSubject("Confirmación de Pedido #" + order.getOrderNumber() + " - " + appName);
            
            // Crear el contexto para el template
            Context context = new Context();
            context.setVariable("order", order);
            context.setVariable("orderNumber", order.getOrderNumber());
            context.setVariable("customerName", order.getUser().getFirstName() + " " + order.getUser().getLastName());
            context.setVariable("orderDate", order.getCreatedAt().format(DATE_FORMATTER));
            context.setVariable("estimatedDelivery", order.getEstimatedDeliveryTime().format(DATE_FORMATTER));
            context.setVariable("items", order.getItems());
            context.setVariable("subtotal", formatCurrency(order.getSubtotal()));
            context.setVariable("deliveryFee", formatCurrency(order.getDeliveryFee()));
            context.setVariable("total", formatCurrency(order.getTotal()));
            context.setVariable("address", getFullAddress(order));
            context.setVariable("paymentMethod", order.getPaymentMethod().getDisplayName());
            context.setVariable("appName", appName);
            
            // Procesar el template HTML
            String htmlContent = templateEngine.process("email/order-confirmation", context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            
            log.info("Correo de confirmación enviado exitosamente a: {}", order.getReceiptEmail());
            
        } catch (MessagingException e) {
            log.error("Error al enviar correo de confirmación", e);
            throw new RuntimeException("Error al enviar correo de confirmación", e);
        }
    }
    
    /**
     * Envía notificación de cambio de estado del pedido
     */
    @Async
    public void sendOrderStatusUpdate(Order order) {
        try {
            log.info("Enviando notificación de cambio de estado para pedido: {}", order.getOrderNumber());
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(order.getUser().getEmail());
            helper.setSubject("Actualización de Pedido #" + order.getOrderNumber() + " - " + appName);
            
            Context context = new Context();
            context.setVariable("orderNumber", order.getOrderNumber());
            context.setVariable("customerName", order.getUser().getFirstName());
            context.setVariable("status", order.getStatus().getDisplayName());
            context.setVariable("appName", appName);
            
            String htmlContent = templateEngine.process("email/order-status-update", context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            
            log.info("Notificación de estado enviada exitosamente");
            
        } catch (MessagingException e) {
            log.error("Error al enviar notificación de estado", e);
        }
    }
    
    /**
     * Envía correo simple de texto plano (fallback)
     */
    @Async
    public void sendSimpleOrderConfirmation(Order order) {
        try {
            log.info("Enviando correo simple de confirmación para pedido: {}", order.getOrderNumber());
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(order.getReceiptEmail());
            helper.setSubject("Confirmación de Pedido #" + order.getOrderNumber());
            
            StringBuilder emailBody = new StringBuilder();
            emailBody.append("Hola ").append(order.getUser().getFirstName()).append(",\n\n");
            emailBody.append("Tu pedido ha sido confirmado.\n\n");
            emailBody.append("Número de Pedido: ").append(order.getOrderNumber()).append("\n");
            emailBody.append("Fecha: ").append(order.getCreatedAt().format(DATE_FORMATTER)).append("\n");
            emailBody.append("Total: S/ ").append(order.getTotal()).append("\n\n");
            
            emailBody.append("Items:\n");
            for (OrderItem item : order.getItems()) {
                emailBody.append("- ")
                    .append(item.getDish().getName())
                    .append(" x")
                    .append(item.getQuantity())
                    .append(" - S/ ")
                    .append(item.getSubtotal())
                    .append("\n");
            }
            
            emailBody.append("\nDirección de entrega:\n");
            emailBody.append(getFullAddress(order)).append("\n\n");
            emailBody.append("Tiempo estimado de entrega: ").append(order.getEstimatedDeliveryTime().format(DATE_FORMATTER)).append("\n\n");
            emailBody.append("Gracias por tu preferencia,\n");
            emailBody.append(appName);
            
            helper.setText(emailBody.toString());
            
            mailSender.send(message);
            
            log.info("Correo simple enviado exitosamente");
            
        } catch (MessagingException e) {
            log.error("Error al enviar correo simple", e);
        }
    }
    
    // ============= MÉTODOS PRIVADOS =============
    
    private String formatCurrency(BigDecimal amount) {
        return String.format("S/ %.2f", amount);
    }
    
    private String getFullAddress(Order order) {
        StringBuilder address = new StringBuilder();
        address.append(order.getAddress().getStreet());
        if (order.getAddress().getNumber() != null) {
            address.append(" ").append(order.getAddress().getNumber());
        }
        address.append(", ").append(order.getAddress().getDistrict().getNombre());
        address.append(", ").append(order.getAddress().getCity().getNombre());
        if (order.getAddress().getReference() != null) {
            address.append(" (").append(order.getAddress().getReference()).append(")");
        }
        return address.toString();
    }
}
