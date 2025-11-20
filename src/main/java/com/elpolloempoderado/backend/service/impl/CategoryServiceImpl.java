package com.elpolloempoderado.backend.service.impl;

import com.elpolloempoderado.backend.dto.CategoryRequest;
import com.elpolloempoderado.backend.dto.CategoryResponse;
import com.elpolloempoderado.backend.exception.BadRequestException;
import com.elpolloempoderado.backend.exception.ResourceNotFoundException;
import com.elpolloempoderado.backend.model.Category;
import com.elpolloempoderado.backend.repository.CategoryRepository;
import com.elpolloempoderado.backend.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repo;

    public CategoryServiceImpl(CategoryRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return repo.findAll()
                .stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        Category c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado la categoría con id: " + id));
        return CategoryResponse.fromEntity(c);
    }

    @Override
    public CategoryResponse create(CategoryRequest request) {
        if (repo.existsByName(request.getName())) {
            throw new BadRequestException("El nombre de la categoría ya existe: " + request.getName());
        }
        Category toSave = request.toEntity();
        Category saved = repo.save(toSave);
        return CategoryResponse.fromEntity(saved);
    }

    @Override
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado la categoría con id: " + id));

        String newName = request.getName();
        if (!existing.getName().equalsIgnoreCase(newName) && repo.existsByName(newName)) {
            throw new BadRequestException("El nombre de la categoría ya existe: " + newName);
        }

        existing.setName(newName);
        existing.setDescription(request.getDescription());
        Category updated = repo.save(existing);
        return CategoryResponse.fromEntity(updated);
    }

    @Override
    public void delete(Long id) {
        Category existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado la categoría con id: " + id));
        repo.delete(existing);
    }
}