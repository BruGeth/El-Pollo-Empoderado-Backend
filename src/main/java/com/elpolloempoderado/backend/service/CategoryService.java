package com.elpolloempoderado.backend.service;

import com.elpolloempoderado.backend.dto.CategoryRequest;
import com.elpolloempoderado.backend.dto.CategoryResponse;
import java.util.List;

public interface CategoryService {
    List<CategoryResponse> findAll();
    CategoryResponse findById(Long id);
    CategoryResponse create(CategoryRequest request);
    CategoryResponse update(Long id, CategoryRequest request);
    void delete(Long id);
}