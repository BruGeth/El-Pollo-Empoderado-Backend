package com.elpolloempoderado.backend.service.impl;

import com.elpolloempoderado.backend.dto.CategoryRequest;
import com.elpolloempoderado.backend.dto.CategoryResponse;
import com.elpolloempoderado.backend.exception.BadRequestException;
import com.elpolloempoderado.backend.exception.ResourceNotFoundException;
import com.elpolloempoderado.backend.model.Category;
import com.elpolloempoderado.backend.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para CategoryServiceImpl
 * Usa @ExtendWith(MockitoExtension.class) para habilitar mocks
 * NO interactúa con la base de datos real - todo es mockeado
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService - Pruebas Unitarias")
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category1;
    private Category category2;
    private CategoryRequest validRequest;

    @BeforeEach
    void setUp() {
        // Datos de prueba reutilizables
        category1 = new Category("Entradas", "Platos de entrada");
        category1.setId(1L);

        category2 = new Category("Postres", "Dulces y helados");
        category2.setId(2L);

        validRequest = new CategoryRequest("Bebidas", "Bebidas frías y calientes");
    }

    // ==================== FIND ALL ====================

    @Test
    @DisplayName("findAll() debe retornar lista de categorías")
    void testFindAll() {
        // Given
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));

        // When
        List<CategoryResponse> result = categoryService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Entradas");
        assertThat(result.get(1).getName()).isEqualTo("Postres");
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll() debe retornar lista vacía si no hay categorías")
    void testFindAll_EmptyList() {
        // Given
        when(categoryRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<CategoryResponse> result = categoryService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(categoryRepository, times(1)).findAll();
    }

    // ==================== FIND BY ID ====================

    @Test
    @DisplayName("findById() debe retornar categoría cuando existe")
    void testFindById_Success() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));

        // When
        CategoryResponse result = categoryService.findById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Entradas");
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById() debe lanzar ResourceNotFoundException cuando no existe")
    void testFindById_NotFound() {
        // Given
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> categoryService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se ha encontrado la categoría con id: 99");
        
        verify(categoryRepository, times(1)).findById(99L);
    }

    // ==================== CREATE ====================

    @Test
    @DisplayName("create() debe crear categoría exitosamente")
    void testCreate_Success() {
        // Given
        Category newCategory = validRequest.toEntity();
        newCategory.setId(3L);
        
        when(categoryRepository.existsByName("Bebidas")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);

        // When
        CategoryResponse result = categoryService.create(validRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getName()).isEqualTo("Bebidas");
        assertThat(result.getDescription()).isEqualTo("Bebidas frías y calientes");
        
        verify(categoryRepository, times(1)).existsByName("Bebidas");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("create() debe lanzar BadRequestException si el nombre ya existe")
    void testCreate_DuplicateName() {
        // Given
        when(categoryRepository.existsByName("Bebidas")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> categoryService.create(validRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("El nombre de la categoría ya existe: Bebidas");
        
        verify(categoryRepository, times(1)).existsByName("Bebidas");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    // ==================== UPDATE ====================

    @Test
    @DisplayName("update() debe actualizar categoría exitosamente")
    void testUpdate_Success() {
        // Given
        CategoryRequest updateRequest = new CategoryRequest("Entradas Gourmet", "Entradas especiales");
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
        when(categoryRepository.existsByName("Entradas Gourmet")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CategoryResponse result = categoryService.update(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Entradas Gourmet");
        assertThat(result.getDescription()).isEqualTo("Entradas especiales");
        
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).existsByName("Entradas Gourmet");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("update() debe permitir actualizar sin cambiar el nombre")
    void testUpdate_SameNameDifferentCase() {
        // Given
        CategoryRequest updateRequest = new CategoryRequest("ENTRADAS", "Nueva descripción");
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
        // No se llama existsByName porque el nombre es el mismo (case insensitive)
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CategoryResponse result = categoryService.update(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("ENTRADAS");
        assertThat(result.getDescription()).isEqualTo("Nueva descripción");
        
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, never()).existsByName(anyString());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("update() debe lanzar ResourceNotFoundException si no existe la categoría")
    void testUpdate_NotFound() {
        // Given
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> categoryService.update(99L, validRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se ha encontrado la categoría con id: 99");
        
        verify(categoryRepository, times(1)).findById(99L);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("update() debe lanzar BadRequestException si el nuevo nombre ya existe")
    void testUpdate_DuplicateName() {
        // Given
        CategoryRequest updateRequest = new CategoryRequest("Postres", "Nueva descripción");
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
        when(categoryRepository.existsByName("Postres")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> categoryService.update(1L, updateRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("El nombre de la categoría ya existe: Postres");
        
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).existsByName("Postres");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    // ==================== DELETE ====================

    @Test
    @DisplayName("delete() debe eliminar categoría exitosamente")
    void testDelete_Success() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
        doNothing().when(categoryRepository).delete(category1);

        // When
        categoryService.delete(1L);

        // Then
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).delete(category1);
    }

    @Test
    @DisplayName("delete() debe lanzar ResourceNotFoundException si no existe la categoría")
    void testDelete_NotFound() {
        // Given
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> categoryService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se ha encontrado la categoría con id: 99");
        
        verify(categoryRepository, times(1)).findById(99L);
        verify(categoryRepository, never()).delete(any(Category.class));
    }
}
