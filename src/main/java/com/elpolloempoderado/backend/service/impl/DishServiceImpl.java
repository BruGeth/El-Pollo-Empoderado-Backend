package com.elpolloempoderado.backend.service.impl;

import com.elpolloempoderado.backend.dto.DishRequest;
import com.elpolloempoderado.backend.dto.DishResponse;
import com.elpolloempoderado.backend.exception.ResourceNotFoundException;
import com.elpolloempoderado.backend.model.Category;
import com.elpolloempoderado.backend.model.Dish;
import com.elpolloempoderado.backend.repository.CategoryRepository;
import com.elpolloempoderado.backend.repository.DishRepository;
import com.elpolloempoderado.backend.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DishServiceImpl implements DishService {
    
    private final DishRepository dishRepository;
    private final CategoryRepository categoryRepository;
    
    @Override
    public List<DishResponse> findAll() {
        return dishRepository.findAll()
                .stream()
                .map(DishResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public DishResponse findById(Long id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plato no encontrado con ID: " + id));
        return DishResponse.fromEntity(dish);
    }
    
    @Override
    public List<DishResponse> findByCategoryId(Long categoryId) {
        // Validar que la categoría existe
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + categoryId);
        }
        
        return dishRepository.findByCategoryId(categoryId)
                .stream()
                .map(DishResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public DishResponse create(DishRequest request) {
        // Validar que la categoría existe
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + request.getCategoryId()));
        
        Dish dish = new Dish();
        dish.setName(request.getName());
        dish.setDescription(request.getDescription());
        dish.setPrice(request.getPrice());
        dish.setOriginalPrice(request.getOriginalPrice());
        dish.setImageUrl(request.getImageUrl());
        dish.setCategory(category);
        
        Dish savedDish = dishRepository.save(dish);
        return DishResponse.fromEntity(savedDish);
    }
    
    @Override
    @Transactional
    public DishResponse update(Long id, DishRequest request) {
        // Verificar que el plato existe
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plato no encontrado con ID: " + id));
        
        // Validar que la categoría existe
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + request.getCategoryId()));
        
        dish.setName(request.getName());
        dish.setDescription(request.getDescription());
        dish.setPrice(request.getPrice());
        dish.setOriginalPrice(request.getOriginalPrice());
        dish.setImageUrl(request.getImageUrl());
        dish.setCategory(category);
        
        Dish updatedDish = dishRepository.save(dish);
        return DishResponse.fromEntity(updatedDish);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        if (!dishRepository.existsById(id)) {
            throw new ResourceNotFoundException("Plato no encontrado con ID: " + id);
        }
        dishRepository.deleteById(id);
    }
}
