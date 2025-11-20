package com.elpolloempoderado.backend.service.impl;

import com.elpolloempoderado.backend.dto.MenuCategoryResponse;
import com.elpolloempoderado.backend.model.Category;
import com.elpolloempoderado.backend.model.Dish;
import com.elpolloempoderado.backend.repository.MenuRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceImplTest {
    
    @Mock
    private MenuRepository menuRepository;
    
    @InjectMocks
    private MenuServiceImpl menuService;
    
    @Test
    void testGetFullMenu_WithMultipleCategoriesAndDishes() {
        // Given
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Platos Principales");
        category1.setDescription("Deliciosas comidas");
        
        Dish dish1 = new Dish();
        dish1.setId(1L);
        dish1.setName("Pollo a la Brasa");
        dish1.setDescription("Delicioso pollo asado");
        dish1.setPrice(new BigDecimal("25.99"));
        dish1.setImageUrl("http://example.com/pollo.jpg");
        dish1.setCategory(category1);
        
        Dish dish2 = new Dish();
        dish2.setId(2L);
        dish2.setName("Arroz con Pollo");
        dish2.setDescription("Tradicional arroz con pollo");
        dish2.setPrice(new BigDecimal("18.50"));
        dish2.setImageUrl("http://example.com/arroz.jpg");
        dish2.setCategory(category1);
        
        category1.getDishes().add(dish1);
        category1.getDishes().add(dish2);
        
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Postres");
        category2.setDescription("Dulces deliciosos");
        
        Dish dish3 = new Dish();
        dish3.setId(3L);
        dish3.setName("Helado");
        dish3.setDescription("Helado de vainilla");
        dish3.setPrice(new BigDecimal("8.00"));
        dish3.setImageUrl("http://example.com/helado.jpg");
        dish3.setCategory(category2);
        
        category2.getDishes().add(dish3);
        
        when(menuRepository.findAllCategoriesWithDishes())
                .thenReturn(Arrays.asList(category1, category2));
        
        // When
        List<MenuCategoryResponse> menu = menuService.getFullMenu();
        
        // Then
        assertThat(menu).hasSize(2);
        
        // Verificar primera categoría
        MenuCategoryResponse firstCategory = menu.get(0);
        assertThat(firstCategory.getName()).isEqualTo("Platos Principales");
        assertThat(firstCategory.getDescription()).isEqualTo("Deliciosas comidas");
        assertThat(firstCategory.getDishes()).hasSize(2);
        
        // Verificar que los platos están ordenados alfabéticamente
        assertThat(firstCategory.getDishes().get(0).getName()).isEqualTo("Arroz con Pollo");
        assertThat(firstCategory.getDishes().get(1).getName()).isEqualTo("Pollo a la Brasa");
        
        // Verificar segunda categoría
        MenuCategoryResponse secondCategory = menu.get(1);
        assertThat(secondCategory.getName()).isEqualTo("Postres");
        assertThat(secondCategory.getDishes()).hasSize(1);
        assertThat(secondCategory.getDishes().get(0).getName()).isEqualTo("Helado");
        
        verify(menuRepository, times(1)).findAllCategoriesWithDishes();
    }
    
    @Test
    void testGetFullMenu_WithEmptyCategories() {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Categoría Vacía");
        category.setDescription("Sin platos");
        
        when(menuRepository.findAllCategoriesWithDishes())
                .thenReturn(Arrays.asList(category));
        
        // When
        List<MenuCategoryResponse> menu = menuService.getFullMenu();
        
        // Then
        assertThat(menu).hasSize(1);
        assertThat(menu.get(0).getDishes()).isEmpty();
        
        verify(menuRepository, times(1)).findAllCategoriesWithDishes();
    }
    
    @Test
    void testGetFullMenu_WithNoCategories() {
        // Given
        when(menuRepository.findAllCategoriesWithDishes())
                .thenReturn(Arrays.asList());
        
        // When
        List<MenuCategoryResponse> menu = menuService.getFullMenu();
        
        // Then
        assertThat(menu).isEmpty();
        
        verify(menuRepository, times(1)).findAllCategoriesWithDishes();
    }
    
    @Test
    void testGetFullMenu_DishesAreOrderedAlphabetically() {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        
        Dish dishZ = new Dish();
        dishZ.setId(1L);
        dishZ.setName("Zebra Cake");
        dishZ.setPrice(new BigDecimal("10.00"));
        dishZ.setCategory(category);
        
        Dish dishA = new Dish();
        dishA.setId(2L);
        dishA.setName("Apple Pie");
        dishA.setPrice(new BigDecimal("8.00"));
        dishA.setCategory(category);
        
        Dish dishM = new Dish();
        dishM.setId(3L);
        dishM.setName("Mango Smoothie");
        dishM.setPrice(new BigDecimal("6.00"));
        dishM.setCategory(category);
        
        category.getDishes().addAll(Arrays.asList(dishZ, dishA, dishM));
        
        when(menuRepository.findAllCategoriesWithDishes())
                .thenReturn(Arrays.asList(category));
        
        // When
        List<MenuCategoryResponse> menu = menuService.getFullMenu();
        
        // Then
        assertThat(menu).hasSize(1);
        List<String> dishNames = menu.get(0).getDishes().stream()
                .map(d -> d.getName())
                .toList();
        
        assertThat(dishNames).containsExactly("Apple Pie", "Mango Smoothie", "Zebra Cake");
        
        verify(menuRepository, times(1)).findAllCategoriesWithDishes();
    }
    
    @Test
    void testGetFullMenu_VerifyDishDataIntegrity() {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        
        Dish dish = new Dish();
        dish.setId(10L);
        dish.setName("Test Dish");
        dish.setDescription("Test Description");
        dish.setPrice(new BigDecimal("15.75"));
        dish.setImageUrl("http://example.com/test.jpg");
        dish.setCategory(category);
        
        category.getDishes().add(dish);
        
        when(menuRepository.findAllCategoriesWithDishes())
                .thenReturn(Arrays.asList(category));
        
        // When
        List<MenuCategoryResponse> menu = menuService.getFullMenu();
        
        // Then
        assertThat(menu).hasSize(1);
        assertThat(menu.get(0).getDishes()).hasSize(1);
        
        var dishResponse = menu.get(0).getDishes().get(0);
        assertThat(dishResponse.getId()).isEqualTo(10L);
        assertThat(dishResponse.getName()).isEqualTo("Test Dish");
        assertThat(dishResponse.getDescription()).isEqualTo("Test Description");
        assertThat(dishResponse.getPrice()).isEqualByComparingTo(new BigDecimal("15.75"));
        assertThat(dishResponse.getImageUrl()).isEqualTo("http://example.com/test.jpg");
        
        verify(menuRepository, times(1)).findAllCategoriesWithDishes();
    }
}
