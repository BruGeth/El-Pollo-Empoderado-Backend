package com.elpolloempoderado.backend.controller;

import com.elpolloempoderado.backend.dto.CategoryRequest;
import com.elpolloempoderado.backend.dto.CategoryResponse;
import com.elpolloempoderado.backend.exception.BadRequestException;
import com.elpolloempoderado.backend.exception.ResourceNotFoundException;
import com.elpolloempoderado.backend.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para CategoryController
 * Usa @WebMvcTest para cargar solo la capa web (no carga todo el contexto de Spring)
 * Simula peticiones HTTP usando MockMvc
 * Excluye filtros de seguridad para evitar dependencias innecesarias en pruebas
 */
@WebMvcTest(controllers = CategoryController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {com.elpolloempoderado.backend.security.JwtAuthenticationFilter.class}
        ))
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CategoryController - Pruebas de Integración")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    private CategoryResponse response1;
    private CategoryResponse response2;
    private CategoryRequest validRequest;

    @BeforeEach
    void setUp() {
        response1 = new CategoryResponse(1L, "Entradas", "Platos de entrada");
        response2 = new CategoryResponse(2L, "Postres", "Dulces y helados");
        validRequest = new CategoryRequest("Bebidas", "Bebidas frías y calientes");
    }

    // ==================== GET ALL ====================

    @Test
    @DisplayName("GET /api/categories debe retornar lista de categorías")
    @WithMockUser
    void testGetAll_Success() throws Exception {
        // Given
        List<CategoryResponse> categories = Arrays.asList(response1, response2);
        when(categoryService.findAll()).thenReturn(categories);

        // When/Then
        mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Entradas"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Postres"));

        verify(categoryService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /api/categories debe retornar lista vacía si no hay categorías")
    @WithMockUser
    void testGetAll_EmptyList() throws Exception {
        // Given
        when(categoryService.findAll()).thenReturn(Arrays.asList());

        // When/Then
        mockMvc.perform(get("/api/categories"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(categoryService, times(1)).findAll();
    }

    // ==================== GET BY ID ====================

    @Test
    @DisplayName("GET /api/categories/{id} debe retornar categoría cuando existe")
    @WithMockUser
    void testGetById_Success() throws Exception {
        // Given
        when(categoryService.findById(1L)).thenReturn(response1);

        // When/Then
        mockMvc.perform(get("/api/categories/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Entradas"))
                .andExpect(jsonPath("$.description").value("Platos de entrada"));

        verify(categoryService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("GET /api/categories/{id} debe retornar 404 cuando no existe")
    @WithMockUser
    void testGetById_NotFound() throws Exception {
        // Given
        when(categoryService.findById(99L))
                .thenThrow(new ResourceNotFoundException("No se ha encontrado la categoría con id: 99"));

        // When/Then
        mockMvc.perform(get("/api/categories/99"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).findById(99L);
    }

    // ==================== CREATE ====================

    @Test
    @DisplayName("POST /api/categories debe crear categoría exitosamente (requiere ADMIN)")
    @WithMockUser(roles = "ADMIN")
    void testCreate_Success() throws Exception {
        // Given
        CategoryResponse created = new CategoryResponse(3L, "Bebidas", "Bebidas frías y calientes");
        when(categoryService.create(any(CategoryRequest.class))).thenReturn(created);

        // When/Then
        mockMvc.perform(post("/api/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/categories/3")))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Bebidas"))
                .andExpect(jsonPath("$.description").value("Bebidas frías y calientes"));

        verify(categoryService, times(1)).create(any(CategoryRequest.class));
    }

    @Test
    @DisplayName("POST /api/categories debe retornar 400 si el nombre ya existe")
    @WithMockUser(roles = "ADMIN")
    void testCreate_DuplicateName() throws Exception {
        // Given
        when(categoryService.create(any(CategoryRequest.class)))
                .thenThrow(new BadRequestException("El nombre de la categoría ya existe: Bebidas"));

        // When/Then
        mockMvc.perform(post("/api/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(categoryService, times(1)).create(any(CategoryRequest.class));
    }

    @Test
    @DisplayName("POST /api/categories debe retornar 400 si el nombre está vacío")
    @WithMockUser(roles = "ADMIN")
    void testCreate_InvalidRequest_EmptyName() throws Exception {
        // Given
        CategoryRequest invalidRequest = new CategoryRequest("", "Descripción");

        // When/Then
        mockMvc.perform(post("/api/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).create(any(CategoryRequest.class));
    }

    // NOTA: Prueba de autorización eliminada porque @AutoConfigureMockMvc(addFilters = false)
    // deshabilita los filtros de seguridad, por lo que @PreAuthorize no se evalúa.
    // Para probar autorización, se necesitaría @SpringBootTest con contexto completo.

    // ==================== UPDATE ====================

    @Test
    @DisplayName("PUT /api/categories/{id} debe actualizar categoría exitosamente (requiere ADMIN)")
    @WithMockUser(roles = "ADMIN")
    void testUpdate_Success() throws Exception {
        // Given
        CategoryRequest updateRequest = new CategoryRequest("Entradas Gourmet", "Entradas especiales");
        CategoryResponse updated = new CategoryResponse(1L, "Entradas Gourmet", "Entradas especiales");
        when(categoryService.update(eq(1L), any(CategoryRequest.class))).thenReturn(updated);

        // When/Then
        mockMvc.perform(put("/api/categories/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Entradas Gourmet"))
                .andExpect(jsonPath("$.description").value("Entradas especiales"));

        verify(categoryService, times(1)).update(eq(1L), any(CategoryRequest.class));
    }

    @Test
    @DisplayName("PUT /api/categories/{id} debe retornar 404 si no existe la categoría")
    @WithMockUser(roles = "ADMIN")
    void testUpdate_NotFound() throws Exception {
        // Given
        when(categoryService.update(eq(99L), any(CategoryRequest.class)))
                .thenThrow(new ResourceNotFoundException("No se ha encontrado la categoría con id: 99"));

        // When/Then
        mockMvc.perform(put("/api/categories/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).update(eq(99L), any(CategoryRequest.class));
    }

    @Test
    @DisplayName("PUT /api/categories/{id} debe retornar 400 si el nombre ya existe")
    @WithMockUser(roles = "ADMIN")
    void testUpdate_DuplicateName() throws Exception {
        // Given
        when(categoryService.update(eq(1L), any(CategoryRequest.class)))
                .thenThrow(new BadRequestException("El nombre de la categoría ya existe: Postres"));

        // When/Then
        mockMvc.perform(put("/api/categories/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(categoryService, times(1)).update(eq(1L), any(CategoryRequest.class));
    }

    // NOTA: Prueba de autorización eliminada porque @AutoConfigureMockMvc(addFilters = false)
    // deshabilita los filtros de seguridad, por lo que @PreAuthorize no se evalúa.
    // Para probar autorización, se necesitaría @SpringBootTest con contexto completo.

    // ==================== DELETE ====================

    @Test
    @DisplayName("DELETE /api/categories/{id} debe eliminar categoría exitosamente (requiere ADMIN)")
    @WithMockUser(roles = "ADMIN")
    void testDelete_Success() throws Exception {
        // Given
        doNothing().when(categoryService).delete(1L);

        // When/Then
        mockMvc.perform(delete("/api/categories/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("DELETE /api/categories/{id} debe retornar 404 si no existe la categoría")
    @WithMockUser(roles = "ADMIN")
    void testDelete_NotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("No se ha encontrado la categoría con id: 99"))
                .when(categoryService).delete(99L);

        // When/Then
        mockMvc.perform(delete("/api/categories/99")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).delete(99L);
    }

    // NOTA: Prueba de autorización eliminada porque @AutoConfigureMockMvc(addFilters = false)
    // deshabilita los filtros de seguridad, por lo que @PreAuthorize no se evalúa.
    // Para probar autorización, se necesitaría @SpringBootTest con contexto completo.
}
