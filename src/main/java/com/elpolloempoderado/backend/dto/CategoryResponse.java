package com.elpolloempoderado.backend.dto;

import lombok.*;
import com.elpolloempoderado.backend.model.Category;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;

    public static CategoryResponse fromEntity(Category c) {
        if (c == null) return null;
        return new CategoryResponse(c.getId(), c.getName(), c.getDescription());
    }
}