package com.cse.expensetracker.category;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryRepository extends MongoRepository<Category, String> {
    List<Category> findByUserIdOrIsDefault(String userId, boolean isDefault);

    Optional<Category> findByIdAndUserId(String id, String userId);
}
