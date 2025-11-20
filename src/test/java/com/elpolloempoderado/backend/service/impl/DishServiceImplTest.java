package com.elpolloempoderado.backend.service.impl;

import com.elpolloempoderado.backend.dto.DishRequest;
import com.elpolloempoderado.backend.dto.DishResponse;
import com.elpolloempoderado.backend.exception.ResourceNotFoundException;
import com.elpolloempoderado.backend.model.Category;
import com.elpolloempoderado.backend.model.Dish;
import com.elpolloempoderado.backend.repository.CategoryRepository;
import com.elpolloempoderado.backend.repository.DishRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishServiceImplTest {
    
    @Mock
    private DishRepository dishRepository;
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @InjectMocks
    private DishServiceImpl dishService;
    
    @Test
    void testFindAll() {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Platos Principales");
        
        Dish dish1 = new Dish();
        dish1.setId(1L);
        dish1.setName("Pollo a la Brasa");
        dish1.setPrice(new BigDecimal("25.99"));
        dish1.setCategory(category);
        
        Dish dish2 = new Dish();
        dish2.setId(2L);
        dish2.setName("Arroz con Pollo");
        dish2.setPrice(new BigDecimal("18.50"));
        dish2.setCategory(category);
        
        when(dishRepository.findAll()).thenReturn(Arrays.asList(dish1, dish2));
        
        // When
        List<DishResponse> result = dishService.findAll();
        
        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Pollo a la Brasa");
        assertThat(result.get(1).getName()).isEqualTo("Arroz con Pollo");
        verify(dishRepository, times(1)).findAll();
    }
    
    @Test
    void testFindById_Success() {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Platos Principales");
        
        Dish dish = new Dish();
        dish.setId(1L);
        dish.setName("Pollo a la Brasa");
        dish.setDescription("Delicioso pollo asado");
        dish.setPrice(new BigDecimal("25.99"));
        dish.setImageUrl("http://example.com/pollo.jpg");
        dish.setCategory(category);
        
        when(dishRepository.findById(1L)).thenReturn(Optional.of(dish));
        
        // When
        DishResponse result = dishService.findById(1L);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Pollo a la Brasa");
        assertThat(result.getCategoryId()).isEqualTo(1L);
        verify(dishRepository, times(1)).findById(1L);
    }
    
    @Test
    void testFindById_NotFound() {
        // Given
        when(dishRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> dishService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Plato no encontrado con ID: 999");
        
        verify(dishRepository, times(1)).findById(999L);
    }
    
    @Test
    void testFindByCategoryId_Success() {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Platos Principales");
        
        Dish dish1 = new Dish();
        dish1.setId(1L);
        dish1.setName("Pollo a la Brasa");
        dish1.setPrice(new BigDecimal("25.99"));
        dish1.setCategory(category);
        
        Dish dish2 = new Dish();
        dish2.setId(2L);
        dish2.setName("Arroz con Pollo");
        dish2.setPrice(new BigDecimal("18.50"));
        dish2.setCategory(category);
        
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(dishRepository.findByCategoryId(1L)).thenReturn(Arrays.asList(dish1, dish2));
        
        // When
        List<DishResponse> result = dishService.findByCategoryId(1L);
        
        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(DishResponse::getName)
                .containsExactlyInAnyOrder("Pollo a la Brasa", "Arroz con Pollo");
        
        verify(categoryRepository, times(1)).existsById(1L);
        verify(dishRepository, times(1)).findByCategoryId(1L);
    }
    
    @Test
    void testFindByCategoryId_CategoryNotFound() {
        // Given
        when(categoryRepository.existsById(anyLong())).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> dishService.findByCategoryId(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Categoría no encontrada con ID: 999");
        
        verify(categoryRepository, times(1)).existsById(999L);
        verify(dishRepository, never()).findByCategoryId(anyLong());
    }
    
    @Test
    void testCreate_Success() {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Platos Principales");
        
        DishRequest request = new DishRequest();
        request.setName("Pollo a la Brasa");
        request.setDescription("Delicioso pollo asado");
        request.setPrice(new BigDecimal("25.99"));
        request.setImageUrl("http://example.com/pollo.jpg");
        request.setCategoryId(1L);
        
        Dish savedDish = new Dish();
        savedDish.setId(1L);
        savedDish.setName(request.getName());
        savedDish.setDescription(request.getDescription());
        savedDish.setPrice(request.getPrice());
        savedDish.setImageUrl(request.getImageUrl());
        savedDish.setCategory(category);
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(dishRepository.save(any(Dish.class))).thenReturn(savedDish);
        
        // When
        DishResponse result = dishService.create(request);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Pollo a la Brasa");
        assertThat(result.getCategoryId()).isEqualTo(1L);
        
        verify(categoryRepository, times(1)).findById(1L);
        verify(dishRepository, times(1)).save(any(Dish.class));
    }
    
    @Test
    void testCreate_CategoryNotFound() {
        // Given
        DishRequest request = new DishRequest();
        request.setName("Pollo a la Brasa");
        request.setPrice(new BigDecimal("25.99"));
        request.setCategoryId(999L);
        
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> dishService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Categoría no encontrada con ID: 999");
        
        verify(categoryRepository, times(1)).findById(999L);
        verify(dishRepository, never()).save(any(Dish.class));
    }
    
    @Test
    void testUpdate_Success() {
        // Given
        Category oldCategory = new Category();
        oldCategory.setId(1L);
        oldCategory.setName("Categoría Antigua");
        
        Category newCategory = new Category();
        newCategory.setId(2L);
        newCategory.setName("Categoría Nueva");
        
        Dish existingDish = new Dish();
        existingDish.setId(1L);
        existingDish.setName("Nombre Antiguo");
        existingDish.setPrice(new BigDecimal("20.00"));
        existingDish.setCategory(oldCategory);
        
        DishRequest request = new DishRequest();
        request.setName("Nombre Actualizado");
        request.setDescription("Descripción actualizada");
        request.setPrice(new BigDecimal("25.99"));
        request.setImageUrl("http://example.com/nuevo.jpg");
        request.setCategoryId(2L);
        
        Dish updatedDish = new Dish();
        updatedDish.setId(1L);
        updatedDish.setName(request.getName());
        updatedDish.setDescription(request.getDescription());
        updatedDish.setPrice(request.getPrice());
        updatedDish.setImageUrl(request.getImageUrl());
        updatedDish.setCategory(newCategory);
        
        when(dishRepository.findById(1L)).thenReturn(Optional.of(existingDish));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));
        when(dishRepository.save(any(Dish.class))).thenReturn(updatedDish);
        
        // When
        DishResponse result = dishService.update(1L, request);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Nombre Actualizado");
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("25.99"));
        assertThat(result.getCategoryId()).isEqualTo(2L);
        
        verify(dishRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findById(2L);
        verify(dishRepository, times(1)).save(any(Dish.class));
    }
    
    @Test
    void testUpdate_DishNotFound() {
        // Given
        DishRequest request = new DishRequest();
        request.setName("Plato");
        request.setPrice(new BigDecimal("20.00"));
        request.setCategoryId(1L);
        
        when(dishRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> dishService.update(999L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Plato no encontrado con ID: 999");
        
        verify(dishRepository, times(1)).findById(999L);
        verify(categoryRepository, never()).findById(anyLong());
        verify(dishRepository, never()).save(any(Dish.class));
    }
    
    @Test
    void testUpdate_CategoryNotFound() {
        // Given
        Category oldCategory = new Category();
        oldCategory.setId(1L);
        
        Dish existingDish = new Dish();
        existingDish.setId(1L);
        existingDish.setName("Plato Existente");
        existingDish.setPrice(new BigDecimal("20.00"));
        existingDish.setCategory(oldCategory);
        
        DishRequest request = new DishRequest();
        request.setName("Plato Actualizado");
        request.setPrice(new BigDecimal("25.00"));
        request.setCategoryId(999L);
        
        when(dishRepository.findById(1L)).thenReturn(Optional.of(existingDish));
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> dishService.update(1L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Categoría no encontrada con ID: 999");
        
        verify(dishRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findById(999L);
        verify(dishRepository, never()).save(any(Dish.class));
    }
    
    @Test
    void testDelete_Success() {
        // Given
        when(dishRepository.existsById(1L)).thenReturn(true);
        doNothing().when(dishRepository).deleteById(1L);
        
        // When
        dishService.delete(1L);
        
        // Then
        verify(dishRepository, times(1)).existsById(1L);
        verify(dishRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void testDelete_NotFound() {
        // Given
        when(dishRepository.existsById(anyLong())).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> dishService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Plato no encontrado con ID: 999");
        
        verify(dishRepository, times(1)).existsById(999L);
        verify(dishRepository, never()).deleteById(anyLong());
    }
}
