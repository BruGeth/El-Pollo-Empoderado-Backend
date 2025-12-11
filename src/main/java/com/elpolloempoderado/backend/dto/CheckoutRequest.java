package com.elpolloempoderado.backend.dto;

import com.elpolloempoderado.backend.model.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para crear un pedido (checkout)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {
    
    @NotNull(message = "El ID de la dirección es requerido")
    private Long addressId;
    
    @NotEmpty(message = "El carrito no puede estar vacío")
    @Valid
    private List<CartItemRequest> items;
    
    @NotNull(message = "El método de pago es requerido")
    private PaymentMethod paymentMethod;
    
    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String notes;
}
