package com.elpolloempoderado.backend.controller;

import com.elpolloempoderado.backend.dto.MenuCategoryResponse;
import com.elpolloempoderado.backend.dto.MenuDishResponse;
import com.elpolloempoderado.backend.security.JwtAuthenticationFilter;
import com.elpolloempoderado.backend.service.MenuService;
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
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = MenuController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class MenuControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private MenuService menuService;
    
    @Test
    @WithMockUser
    void testGetFullMenu_Success() throws Exception {
        // Given
        MenuDishResponse dish1 = new MenuDishResponse(
                1L, "Pollo a la Brasa", "Delicioso pollo", new BigDecimal("25.99"), "http://example.com/pollo.jpg"
        );
        
        MenuDishResponse dish2 = new MenuDishResponse(
                2L, "Arroz con Pollo", "Tradicional", new BigDecimal("18.50"), "http://example.com/arroz.jpg"
        );
        
        MenuCategoryResponse category1 = new MenuCategoryResponse(
                1L, "Platos Principales", "Comidas completas", Arrays.asList(dish1, dish2)
        );
        
        MenuDishResponse dish3 = new MenuDishResponse(
                3L, "Helado", "Helado de vainilla", new BigDecimal("8.00"), "http://example.com/helado.jpg"
        );
        
        MenuCategoryResponse category2 = new MenuCategoryResponse(
                2L, "Postres", "Dulces deliciosos", Arrays.asList(dish3)
        );
        
        List<MenuCategoryResponse> menu = Arrays.asList(category1, category2);
        
        when(menuService.getFullMenu()).thenReturn(menu);
        
        // When & Then
        mockMvc.perform(get("/api/menu"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                
                // Verificar primera categoría
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Platos Principales"))
                .andExpect(jsonPath("$[0].description").value("Comidas completas"))
                .andExpect(jsonPath("$[0].dishes", hasSize(2)))
                
                // Verificar primer plato de la primera categoría
                .andExpect(jsonPath("$[0].dishes[0].id").value(1))
                .andExpect(jsonPath("$[0].dishes[0].name").value("Pollo a la Brasa"))
                .andExpect(jsonPath("$[0].dishes[0].description").value("Delicioso pollo"))
                .andExpect(jsonPath("$[0].dishes[0].price").value(25.99))
                .andExpect(jsonPath("$[0].dishes[0].imageUrl").value("http://example.com/pollo.jpg"))
                
                // Verificar segundo plato de la primera categoría
                .andExpect(jsonPath("$[0].dishes[1].id").value(2))
                .andExpect(jsonPath("$[0].dishes[1].name").value("Arroz con Pollo"))
                
                // Verificar segunda categoría
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Postres"))
                .andExpect(jsonPath("$[1].dishes", hasSize(1)))
                .andExpect(jsonPath("$[1].dishes[0].name").value("Helado"));
        
        verify(menuService, times(1)).getFullMenu();
    }
    
    @Test
    @WithMockUser
    void testGetFullMenu_EmptyMenu() throws Exception {
        // Given
        when(menuService.getFullMenu()).thenReturn(Collections.emptyList());
        
        // When & Then
        mockMvc.perform(get("/api/menu"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
        
        verify(menuService, times(1)).getFullMenu();
    }
    
    @Test
    @WithMockUser
    void testGetFullMenu_CategoryWithNoDishes() throws Exception {
        // Given
        MenuCategoryResponse emptyCategory = new MenuCategoryResponse(
                1L, "Categoría Vacía", "Sin platos disponibles", Collections.emptyList()
        );
        
        when(menuService.getFullMenu()).thenReturn(Arrays.asList(emptyCategory));
        
        // When & Then
        mockMvc.perform(get("/api/menu"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Categoría Vacía"))
                .andExpect(jsonPath("$[0].dishes", hasSize(0)));
        
        verify(menuService, times(1)).getFullMenu();
    }
    
    @Test
    @WithMockUser
    void testGetFullMenu_VerifyResponseStructure() throws Exception {
        // Given
        MenuDishResponse dish = new MenuDishResponse(
                1L, "Test Dish", "Test Description", new BigDecimal("10.50"), "http://test.com/image.jpg"
        );
        
        MenuCategoryResponse category = new MenuCategoryResponse(
                1L, "Test Category", "Test Desc", Arrays.asList(dish)
        );
        
        when(menuService.getFullMenu()).thenReturn(Arrays.asList(category));
        
        // When & Then
        mockMvc.perform(get("/api/menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].description").exists())
                .andExpect(jsonPath("$[0].dishes").exists())
                .andExpect(jsonPath("$[0].dishes[0].id").exists())
                .andExpect(jsonPath("$[0].dishes[0].name").exists())
                .andExpect(jsonPath("$[0].dishes[0].description").exists())
                .andExpect(jsonPath("$[0].dishes[0].price").exists())
                .andExpect(jsonPath("$[0].dishes[0].imageUrl").exists())
                // Verificar que NO incluye el campo category en MenuDishResponse
                .andExpect(jsonPath("$[0].dishes[0].category").doesNotExist())
                .andExpect(jsonPath("$[0].dishes[0].categoryId").doesNotExist());
        
        verify(menuService, times(1)).getFullMenu();
    }
}
