package com.cse.expensetracker.category.dto;

import com.cse.expensetracker.category.CategoryType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponse {
    private String id;
    private String name;
    private CategoryType type;
    private boolean isDefault;
}
