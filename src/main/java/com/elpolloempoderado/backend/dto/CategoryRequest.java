package com.elpolloempoderado.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import com.elpolloempoderado.backend.model.Category;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
    @NotBlank
    private String name;
    private String description;

    public Category toEntity() {
        return new Category(name, description);
    }
}