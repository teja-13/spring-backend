package com.cse.expensetracker.category;

import com.cse.expensetracker.category.dto.CategoryRequest;
import com.cse.expensetracker.category.dto.CategoryResponse;
import com.cse.expensetracker.exception.BadRequestException;
import com.cse.expensetracker.exception.ResourceNotFoundException;
import com.cse.expensetracker.user.User;
import com.cse.expensetracker.user.UserService;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public List<CategoryResponse> list(String email) {
        User user = userService.getByEmail(email);
        return categoryRepository.findByUserIdOrIsDefault(user.getId(), true)
                .stream()
                .sorted(Comparator.comparing(Category::isDefault).reversed()
                        .thenComparing(Category::getName))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse create(String email, CategoryRequest request) {
        User user = userService.getByEmail(email);
        Category category = Category.builder()
                .name(request.getName())
                .type(request.getType())
                .userId(user.getId())
                .isDefault(false)
                .createdAt(Instant.now())
                .build();
        return toResponse(categoryRepository.save(category));
    }

    public CategoryResponse update(String email, String id, CategoryRequest request) {
        User user = userService.getByEmail(email);
        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        if (category.isDefault()) {
            throw new BadRequestException("Default categories cannot be edited");
        }
        category.setName(request.getName());
        category.setType(request.getType());
        return toResponse(categoryRepository.save(category));
    }

    public void delete(String email, String id) {
        User user = userService.getByEmail(email);
        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        if (category.isDefault()) {
            throw new BadRequestException("Default categories cannot be deleted");
        }
        categoryRepository.delete(category);
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .isDefault(category.isDefault())
                .build();
    }
}
