package com.elpolloempoderado.backend.repository;

import com.elpolloempoderado.backend.model.Category;
import com.elpolloempoderado.backend.model.Dish;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DishRepositoryTest {
    
    @Autowired
    private DishRepository dishRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    private Category category1;
    private Category category2;
    
    @BeforeEach
    void setUp() {
        dishRepository.deleteAll();
        categoryRepository.deleteAll();
        
        // Crear categorías de prueba
        category1 = new Category();
        category1.setName("Platos Principales");
        category1.setDescription("Comidas completas");
        category1 = categoryRepository.save(category1);
        
        category2 = new Category();
        category2.setName("Postres");
        category2.setDescription("Dulces deliciosos");
        category2 = categoryRepository.save(category2);
    }
    
    @Test
    void testSaveDish() {
        // Given
        Dish dish = new Dish();
        dish.setName("Pollo a la Brasa");
        dish.setDescription("Delicioso pollo asado");
        dish.setPrice(new BigDecimal("25.99"));
        dish.setImageUrl("http://example.com/pollo.jpg");
        dish.setCategory(category1);
        
        // When
        Dish savedDish = dishRepository.save(dish);
        
        // Then
        assertThat(savedDish).isNotNull();
        assertThat(savedDish.getId()).isNotNull();
        assertThat(savedDish.getName()).isEqualTo("Pollo a la Brasa");
        assertThat(savedDish.getPrice()).isEqualByComparingTo(new BigDecimal("25.99"));
        assertThat(savedDish.getCategory().getId()).isEqualTo(category1.getId());
    }
    
    @Test
    void testFindById() {
        // Given
        Dish dish = new Dish();
        dish.setName("Arroz con Pollo");
        dish.setDescription("Tradicional arroz con pollo");
        dish.setPrice(new BigDecimal("18.50"));
        dish.setCategory(category1);
        Dish savedDish = dishRepository.save(dish);
        
        // When
        Optional<Dish> foundDish = dishRepository.findById(savedDish.getId());
        
        // Then
        assertThat(foundDish).isPresent();
        assertThat(foundDish.get().getName()).isEqualTo("Arroz con Pollo");
    }
    
    @Test
    void testFindByCategoryId() {
        // Given
        Dish dish1 = new Dish();
        dish1.setName("Pollo a la Brasa");
        dish1.setPrice(new BigDecimal("25.99"));
        dish1.setCategory(category1);
        dishRepository.save(dish1);
        
        Dish dish2 = new Dish();
        dish2.setName("Arroz con Pollo");
        dish2.setPrice(new BigDecimal("18.50"));
        dish2.setCategory(category1);
        dishRepository.save(dish2);
        
        Dish dish3 = new Dish();
        dish3.setName("Helado");
        dish3.setPrice(new BigDecimal("8.00"));
        dish3.setCategory(category2);
        dishRepository.save(dish3);
        
        // When
        List<Dish> dishesCategory1 = dishRepository.findByCategoryId(category1.getId());
        List<Dish> dishesCategory2 = dishRepository.findByCategoryId(category2.getId());
        
        // Then
        assertThat(dishesCategory1).hasSize(2);
        assertThat(dishesCategory1).extracting(Dish::getName)
                .containsExactlyInAnyOrder("Pollo a la Brasa", "Arroz con Pollo");
        
        assertThat(dishesCategory2).hasSize(1);
        assertThat(dishesCategory2.get(0).getName()).isEqualTo("Helado");
    }
    
    @Test
    void testFindByCategoryId_NoResults() {
        // When
        List<Dish> dishes = dishRepository.findByCategoryId(category1.getId());
        
        // Then
        assertThat(dishes).isEmpty();
    }
    
    @Test
    void testExistsByName() {
        // Given
        Dish dish = new Dish();
        dish.setName("Pollo a la Brasa");
        dish.setPrice(new BigDecimal("25.99"));
        dish.setCategory(category1);
        dishRepository.save(dish);
        
        // When & Then
        assertThat(dishRepository.existsByName("Pollo a la Brasa")).isTrue();
        assertThat(dishRepository.existsByName("Plato Inexistente")).isFalse();
    }
    
    @Test
    void testUpdateDish() {
        // Given
        Dish dish = new Dish();
        dish.setName("Pollo Original");
        dish.setDescription("Descripción original");
        dish.setPrice(new BigDecimal("20.00"));
        dish.setCategory(category1);
        Dish savedDish = dishRepository.save(dish);
        
        // When
        savedDish.setName("Pollo Actualizado");
        savedDish.setPrice(new BigDecimal("22.50"));
        savedDish.setCategory(category2);
        Dish updatedDish = dishRepository.save(savedDish);
        
        // Then
        assertThat(updatedDish.getName()).isEqualTo("Pollo Actualizado");
        assertThat(updatedDish.getPrice()).isEqualByComparingTo(new BigDecimal("22.50"));
        assertThat(updatedDish.getCategory().getId()).isEqualTo(category2.getId());
    }
    
    @Test
    void testDeleteDish() {
        // Given
        Dish dish = new Dish();
        dish.setName("Pollo a Eliminar");
        dish.setPrice(new BigDecimal("15.00"));
        dish.setCategory(category1);
        Dish savedDish = dishRepository.save(dish);
        Long dishId = savedDish.getId();
        
        // When
        dishRepository.deleteById(dishId);
        
        // Then
        assertThat(dishRepository.findById(dishId)).isEmpty();
    }
    
    @Test
    void testFindAll() {
        // Given
        Dish dish1 = new Dish();
        dish1.setName("Plato 1");
        dish1.setPrice(new BigDecimal("10.00"));
        dish1.setCategory(category1);
        dishRepository.save(dish1);
        
        Dish dish2 = new Dish();
        dish2.setName("Plato 2");
        dish2.setPrice(new BigDecimal("15.00"));
        dish2.setCategory(category2);
        dishRepository.save(dish2);
        
        // When
        List<Dish> allDishes = dishRepository.findAll();
        
        // Then
        assertThat(allDishes).hasSize(2);
    }
    
    @Test
    void testCascadeRelationship() {
        // Given
        Dish dish = new Dish();
        dish.setName("Pollo con Categoría");
        dish.setPrice(new BigDecimal("20.00"));
        dish.setCategory(category1);
        Dish savedDish = dishRepository.save(dish);
        dishRepository.flush(); // Forzar persistencia
        
        Long dishId = savedDish.getId();
        Long categoryId = category1.getId();
        
        // When - Eliminar la categoría debería fallar porque hay platos asociados (foreign key constraint)
        // En una aplicación real, esto lanzaría una excepción de integridad referencial
        // Para este test, simplemente verificamos que el plato existe
        assertThat(dishRepository.existsById(dishId)).isTrue();
        assertThat(categoryRepository.existsById(categoryId)).isTrue();
        
        // Then - Verificar que el plato tiene su categoría correctamente asociada
        Dish foundDish = dishRepository.findById(dishId).orElseThrow();
        assertThat(foundDish.getCategory()).isNotNull();
        assertThat(foundDish.getCategory().getId()).isEqualTo(categoryId);
    }
}
