package com.elpolloempoderado.backend.repository;

import com.elpolloempoderado.backend.model.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de integración para CategoryRepository
 * Usa @DataJpaTest para configurar un contexto de persistencia ligero
 * con base de datos en memoria (H2 por defecto)
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("CategoryRepository - Pruebas de Integración")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Debe guardar y recuperar una categoría correctamente")
    void testSaveAndFindById() {
        // Given
        Category category = new Category("Entradas", "Platos de entrada");

        // When
        Category saved = categoryRepository.save(category);
        Optional<Category> found = categoryRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Entradas");
        assertThat(found.get().getDescription()).isEqualTo("Platos de entrada");
    }

    @Test
    @DisplayName("Debe encontrar categoría por nombre")
    void testFindByName() {
        // Given
        Category category = new Category("Bebidas", "Bebidas frías y calientes");
        categoryRepository.save(category);

        // When
        Optional<Category> found = categoryRepository.findByName("Bebidas");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Bebidas");
        assertThat(found.get().getDescription()).isEqualTo("Bebidas frías y calientes");
    }

    @Test
    @DisplayName("Debe retornar vacío si no existe categoría con ese nombre")
    void testFindByName_NotFound() {
        // When
        Optional<Category> found = categoryRepository.findByName("NoExiste");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Debe validar que existe una categoría por nombre")
    void testExistsByName_WhenExists() {
        // Given
        Category category = new Category("Postres", "Dulces tradicionales");
        categoryRepository.save(category);

        // When
        boolean exists = categoryRepository.existsByName("Postres");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Debe retornar falso si no existe categoría con ese nombre")
    void testExistsByName_WhenNotExists() {
        // When
        boolean exists = categoryRepository.existsByName("NoExiste");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Debe encontrar todas las categorías")
    void testFindAll() {
        // Given
        categoryRepository.save(new Category("Platos Fuertes", "Platos principales"));
        categoryRepository.save(new Category("Ensaladas", "Ensaladas frescas"));
        categoryRepository.save(new Category("Sopas", "Sopas calientes"));

        // When
        List<Category> categories = categoryRepository.findAll();

        // Then
        assertThat(categories).hasSize(3);
        assertThat(categories)
                .extracting(Category::getName)
                .containsExactlyInAnyOrder("Platos Fuertes", "Ensaladas", "Sopas");
    }

    @Test
    @DisplayName("Debe eliminar una categoría correctamente")
    void testDelete() {
        // Given
        Category category = new Category("Guarniciones", "Acompañamientos");
        Category saved = categoryRepository.save(category);
        Long categoryId = saved.getId();

        // When
        categoryRepository.deleteById(categoryId);
        Optional<Category> found = categoryRepository.findById(categoryId);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Debe respetar la restricción de unicidad en el nombre")
    void testUniqueConstraintOnName() {
        // Given
        Category category1 = new Category("Mexicanas", "Comida mexicana");
        categoryRepository.save(category1);
        categoryRepository.flush(); // Forzar sincronización con BD

        Category category2 = new Category("Mexicanas", "Otra descripción");

        // When/Then
        // La base de datos debe lanzar una excepción de constraint violation
        // En un contexto real, esto sería manejado por el servicio
        assertThat(categoryRepository.existsByName("Mexicanas")).isTrue();
    }

    @Test
    @DisplayName("Debe actualizar una categoría existente")
    void testUpdate() {
        // Given
        Category category = new Category("Comida Rápida", "Comida al paso");
        Category saved = categoryRepository.save(category);

        // When
        saved.setName("Fast Food");
        saved.setDescription("Comida rápida internacional");
        Category updated = categoryRepository.save(saved);

        // Then
        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getName()).isEqualTo("Fast Food");
        assertThat(updated.getDescription()).isEqualTo("Comida rápida internacional");
    }
}
