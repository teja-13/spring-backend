package com.cse.expensetracker.transaction;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    Optional<Transaction> findByIdAndUserId(String id, String userId);
}
