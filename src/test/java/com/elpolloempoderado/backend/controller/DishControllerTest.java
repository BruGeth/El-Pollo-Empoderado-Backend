package com.elpolloempoderado.backend.controller;

import com.elpolloempoderado.backend.dto.DishRequest;
import com.elpolloempoderado.backend.dto.DishResponse;
import com.elpolloempoderado.backend.exception.ResourceNotFoundException;
import com.elpolloempoderado.backend.security.JwtAuthenticationFilter;
import com.elpolloempoderado.backend.service.DishService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = DishController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class DishControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private DishService dishService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @WithMockUser
    void testGetAllDishes() throws Exception {
        // Given
        DishResponse dish1 = new DishResponse();
        dish1.setId(1L);
        dish1.setName("Pollo a la Brasa");
        dish1.setPrice(new BigDecimal("25.99"));
        dish1.setCategoryId(1L);
        dish1.setCategoryName("Platos Principales");
        
        DishResponse dish2 = new DishResponse();
        dish2.setId(2L);
        dish2.setName("Arroz con Pollo");
        dish2.setPrice(new BigDecimal("18.50"));
        dish2.setCategoryId(1L);
        dish2.setCategoryName("Platos Principales");
        
        List<DishResponse> dishes = Arrays.asList(dish1, dish2);
        
        when(dishService.findAll()).thenReturn(dishes);
        
        // When & Then
        mockMvc.perform(get("/api/dishes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Pollo a la Brasa"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Arroz con Pollo"));
        
        verify(dishService, times(1)).findAll();
    }
    
    @Test
    @WithMockUser
    void testGetDishById_Success() throws Exception {
        // Given
        DishResponse dish = new DishResponse();
        dish.setId(1L);
        dish.setName("Pollo a la Brasa");
        dish.setDescription("Delicioso pollo asado");
        dish.setPrice(new BigDecimal("25.99"));
        dish.setImageUrl("http://example.com/pollo.jpg");
        dish.setCategoryId(1L);
        dish.setCategoryName("Platos Principales");
        
        when(dishService.findById(1L)).thenReturn(dish);
        
        // When & Then
        mockMvc.perform(get("/api/dishes/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Pollo a la Brasa"))
                .andExpect(jsonPath("$.description").value("Delicioso pollo asado"))
                .andExpect(jsonPath("$.price").value(25.99))
                .andExpect(jsonPath("$.categoryId").value(1));
        
        verify(dishService, times(1)).findById(1L);
    }
    
    @Test
    @WithMockUser
    void testGetDishById_NotFound() throws Exception {
        // Given
        when(dishService.findById(999L)).thenThrow(new ResourceNotFoundException("Plato no encontrado con ID: 999"));
        
        // When & Then
        mockMvc.perform(get("/api/dishes/999"))
                .andExpect(status().isNotFound());
        
        verify(dishService, times(1)).findById(999L);
    }
    
    @Test
    @WithMockUser
    void testGetDishesByCategory_Success() throws Exception {
        // Given
        DishResponse dish1 = new DishResponse();
        dish1.setId(1L);
        dish1.setName("Pollo a la Brasa");
        dish1.setPrice(new BigDecimal("25.99"));
        dish1.setCategoryId(1L);
        dish1.setCategoryName("Platos Principales");
        
        DishResponse dish2 = new DishResponse();
        dish2.setId(2L);
        dish2.setName("Arroz con Pollo");
        dish2.setPrice(new BigDecimal("18.50"));
        dish2.setCategoryId(1L);
        dish2.setCategoryName("Platos Principales");
        
        List<DishResponse> dishes = Arrays.asList(dish1, dish2);
        
        when(dishService.findByCategoryId(1L)).thenReturn(dishes);
        
        // When & Then
        mockMvc.perform(get("/api/dishes/category/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].categoryId").value(1))
                .andExpect(jsonPath("$[1].categoryId").value(1));
        
        verify(dishService, times(1)).findByCategoryId(1L);
    }
    
    @Test
    @WithMockUser
    void testGetDishesByCategory_CategoryNotFound() throws Exception {
        // Given
        when(dishService.findByCategoryId(999L)).thenThrow(new ResourceNotFoundException("Categoría no encontrada con ID: 999"));
        
        // When & Then
        mockMvc.perform(get("/api/dishes/category/999"))
                .andExpect(status().isNotFound());
        
        verify(dishService, times(1)).findByCategoryId(999L);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateDish_Success() throws Exception {
        // Given
        DishRequest request = new DishRequest();
        request.setName("Pollo a la Brasa");
        request.setDescription("Delicioso pollo asado");
        request.setPrice(new BigDecimal("25.99"));
        request.setImageUrl("http://example.com/pollo.jpg");
        request.setCategoryId(1L);
        
        DishResponse response = new DishResponse();
        response.setId(1L);
        response.setName(request.getName());
        response.setDescription(request.getDescription());
        response.setPrice(request.getPrice());
        response.setImageUrl(request.getImageUrl());
        response.setCategoryId(1L);
        response.setCategoryName("Platos Principales");
        
        when(dishService.create(any(DishRequest.class))).thenReturn(response);
        
        // When & Then
        mockMvc.perform(post("/api/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Pollo a la Brasa"))
                .andExpect(jsonPath("$.price").value(25.99));
        
        verify(dishService, times(1)).create(any(DishRequest.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateDish_InvalidData() throws Exception {
        // Given - Request inválido (sin nombre)
        DishRequest request = new DishRequest();
        request.setPrice(new BigDecimal("25.99"));
        request.setCategoryId(1L);
        
        // When & Then
        mockMvc.perform(post("/api/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        
        verify(dishService, never()).create(any(DishRequest.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateDish_InvalidPrice() throws Exception {
        // Given - Precio negativo
        DishRequest request = new DishRequest();
        request.setName("Plato Inválido");
        request.setPrice(new BigDecimal("-10.00"));
        request.setCategoryId(1L);
        
        // When & Then
        mockMvc.perform(post("/api/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        
        verify(dishService, never()).create(any(DishRequest.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateDish_Success() throws Exception {
        // Given
        DishRequest request = new DishRequest();
        request.setName("Pollo Actualizado");
        request.setDescription("Descripción actualizada");
        request.setPrice(new BigDecimal("27.99"));
        request.setImageUrl("http://example.com/nuevo.jpg");
        request.setCategoryId(2L);
        
        DishResponse response = new DishResponse();
        response.setId(1L);
        response.setName(request.getName());
        response.setDescription(request.getDescription());
        response.setPrice(request.getPrice());
        response.setImageUrl(request.getImageUrl());
        response.setCategoryId(2L);
        response.setCategoryName("Categoría Nueva");
        
        when(dishService.update(eq(1L), any(DishRequest.class))).thenReturn(response);
        
        // When & Then
        mockMvc.perform(put("/api/dishes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Pollo Actualizado"))
                .andExpect(jsonPath("$.price").value(27.99));
        
        verify(dishService, times(1)).update(eq(1L), any(DishRequest.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateDish_NotFound() throws Exception {
        // Given
        DishRequest request = new DishRequest();
        request.setName("Plato Actualizado");
        request.setPrice(new BigDecimal("25.99"));
        request.setCategoryId(1L);
        
        when(dishService.update(eq(999L), any(DishRequest.class)))
                .thenThrow(new ResourceNotFoundException("Plato no encontrado con ID: 999"));
        
        // When & Then
        mockMvc.perform(put("/api/dishes/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        
        verify(dishService, times(1)).update(eq(999L), any(DishRequest.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteDish_Success() throws Exception {
        // Given
        doNothing().when(dishService).delete(1L);
        
        // When & Then
        mockMvc.perform(delete("/api/dishes/1"))
                .andExpect(status().isNoContent());
        
        verify(dishService, times(1)).delete(1L);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteDish_NotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Plato no encontrado con ID: 999"))
                .when(dishService).delete(999L);
        
        // When & Then
        mockMvc.perform(delete("/api/dishes/999"))
                .andExpect(status().isNotFound());
        
        verify(dishService, times(1)).delete(999L);
    }
}
