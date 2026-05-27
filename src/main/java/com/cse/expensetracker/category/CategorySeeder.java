package com.cse.expensetracker.category;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategorySeeder implements CommandLineRunner {
    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        if (!categoryRepository.findByUserIdOrIsDefault("", true).isEmpty()) {
            return;
        }
        List<Category> defaults = List.of(
                Category.builder().name("Salary").type(CategoryType.INCOME).isDefault(true).createdAt(Instant.now()).build(),
                Category.builder().name("Food").type(CategoryType.EXPENSE).isDefault(true).createdAt(Instant.now()).build(),
                Category.builder().name("Travel").type(CategoryType.EXPENSE).isDefault(true).createdAt(Instant.now()).build(),
                Category.builder().name("Shopping").type(CategoryType.EXPENSE).isDefault(true).createdAt(Instant.now()).build(),
                Category.builder().name("Bills").type(CategoryType.EXPENSE).isDefault(true).createdAt(Instant.now()).build(),
                Category.builder().name("Entertainment").type(CategoryType.EXPENSE).isDefault(true).createdAt(Instant.now()).build(),
                Category.builder().name("Other").type(CategoryType.EXPENSE).isDefault(true).createdAt(Instant.now()).build()
        );
        categoryRepository.saveAll(defaults);
    }
}
