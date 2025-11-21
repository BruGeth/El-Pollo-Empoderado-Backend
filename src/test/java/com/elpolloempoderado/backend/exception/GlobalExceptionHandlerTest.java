package com.elpolloempoderado.backend.exception;

import com.elpolloempoderado.backend.config.GlobalExceptionHandler;
import com.elpolloempoderado.backend.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {
    
    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;
    
    @Mock
    private HttpServletRequest request;
    
    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/test");
    }
    
    @Test
    @DisplayName("Debe manejar ResourceNotFoundException con status 404")
    void testHandleResourceNotFoundException() {
        // Given
        String errorMessage = "Recurso no encontrado";
        ResourceNotFoundException exception = new ResourceNotFoundException(errorMessage);
        
        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNotFound(exception, request);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getError()).isEqualTo("Not Found");
        assertThat(response.getBody().getMessage()).isEqualTo(errorMessage);
        assertThat(response.getBody().getPath()).isEqualTo("/api/test");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }
    
    @Test
    @DisplayName("Debe manejar BadRequestException con status 400")
    void testHandleBadRequestException() {
        // Given
        String errorMessage = "Solicitud incorrecta";
        BadRequestException exception = new BadRequestException(errorMessage);
        
        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadRequest(exception, request);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Bad Request");
        assertThat(response.getBody().getMessage()).isEqualTo(errorMessage);
        assertThat(response.getBody().getPath()).isEqualTo("/api/test");
    }
    
    @Test
    @DisplayName("Debe manejar IllegalArgumentException con status 400")
    void testHandleIllegalArgumentException() {
        // Given
        String errorMessage = "Argumento inválido";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);
        
        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgument(exception, request);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Bad Request");
        assertThat(response.getBody().getMessage()).isEqualTo(errorMessage);
    }
    
    @Test
    @DisplayName("Debe manejar ForbiddenException con status 403")
    void testHandleForbiddenException() {
        // Given
        String errorMessage = "Acceso prohibido";
        ForbiddenException exception = new ForbiddenException(errorMessage);
        
        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleForbidden(exception, request);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(403);
        assertThat(response.getBody().getError()).isEqualTo("Forbidden");
        assertThat(response.getBody().getMessage()).isEqualTo(errorMessage);
    }
    
    @Test
    @DisplayName("Debe manejar MethodArgumentNotValidException con errores de validación")
    void testHandleValidationException() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("dishRequest", "name", "El nombre es requerido");
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));
        
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);
        
        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidation(exception, request);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Validation Error");
        assertThat(response.getBody().getMessage()).isEqualTo("Error de validación en los campos del request");
        assertThat(response.getBody().getValidationErrors()).isNotNull();
        assertThat(response.getBody().getValidationErrors()).hasSize(1);
        assertThat(response.getBody().getValidationErrors().get(0).getField()).isEqualTo("name");
        assertThat(response.getBody().getValidationErrors().get(0).getMessage()).isEqualTo("El nombre es requerido");
    }
    
    @Test
    @DisplayName("Debe manejar Exception genérica con status 500")
    void testHandleGlobalException() {
        // Given
        Exception exception = new RuntimeException("Error inesperado");
        
        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception, request);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getMessage()).isEqualTo("Error interno del servidor");
        assertThat(response.getBody().getPath()).isEqualTo("/api/test");
    }
    
    @Test
    @DisplayName("Debe incluir múltiples errores de validación")
    void testHandleValidationExceptionWithMultipleErrors() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("dishRequest", "name", "El nombre es requerido");
        FieldError fieldError2 = new FieldError("dishRequest", "price", "El precio debe ser mayor a 0");
        when(bindingResult.getFieldErrors()).thenReturn(java.util.Arrays.asList(fieldError1, fieldError2));
        
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);
        
        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidation(exception, request);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getValidationErrors()).hasSize(2);
        assertThat(response.getBody().getValidationErrors().get(0).getField()).isEqualTo("name");
        assertThat(response.getBody().getValidationErrors().get(1).getField()).isEqualTo("price");
    }
}
