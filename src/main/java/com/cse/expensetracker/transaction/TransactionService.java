package com.cse.expensetracker.transaction;

import com.cse.expensetracker.common.PagedResponse;
import com.cse.expensetracker.exception.ResourceNotFoundException;
import com.cse.expensetracker.transaction.dto.TransactionRequest;
import com.cse.expensetracker.transaction.dto.TransactionResponse;
import com.cse.expensetracker.user.User;
import com.cse.expensetracker.user.UserService;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final MongoTemplate mongoTemplate;

    public TransactionResponse create(String email, TransactionRequest request) {
        User user = userService.getByEmail(email);
        Transaction transaction = Transaction.builder()
                .userId(user.getId())
                .type(request.getType())
                .amount(request.getAmount())
                .category(request.getCategory())
                .note(request.getNote())
                .date(request.getDate())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return toResponse(transactionRepository.save(transaction));
    }

    public TransactionResponse getById(String email, String id) {
        User user = userService.getByEmail(email);
        Transaction transaction = transactionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        return toResponse(transaction);
    }

    public TransactionResponse update(String email, String id, TransactionRequest request) {
        User user = userService.getByEmail(email);
        Transaction transaction = transactionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setCategory(request.getCategory());
        transaction.setNote(request.getNote());
        transaction.setDate(request.getDate());
        transaction.setUpdatedAt(Instant.now());
        return toResponse(transactionRepository.save(transaction));
    }

    public void delete(String email, String id) {
        User user = userService.getByEmail(email);
        Transaction transaction = transactionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        transactionRepository.delete(transaction);
    }

    public PagedResponse<TransactionResponse> list(
            String email,
            String search,
            String category,
            TransactionType type,
            LocalDate from,
            LocalDate to,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        User user = userService.getByEmail(email);
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(user.getId()));

        if (search != null && !search.isBlank()) {
            query.addCriteria(Criteria.where("note").regex(search, "i"));
        }
        if (category != null && !category.isBlank()) {
            query.addCriteria(Criteria.where("category").is(category));
        }
        if (type != null) {
            query.addCriteria(Criteria.where("type").is(type));
        }
        if (from != null && to != null) {
            query.addCriteria(Criteria.where("date").gte(from).lte(to));
        } else if (from != null) {
            query.addCriteria(Criteria.where("date").gte(from));
        } else if (to != null) {
            query.addCriteria(Criteria.where("date").lte(to));
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        query.with(PageRequest.of(page, size, sort));

        List<TransactionResponse> items = mongoTemplate.find(query, Transaction.class).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Transaction.class);
        return PagedResponse.<TransactionResponse>builder()
                .items(items)
                .total(total)
                .page(page)
                .size(size)
                .build();
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .category(transaction.getCategory())
                .note(transaction.getNote())
                .date(transaction.getDate())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}
