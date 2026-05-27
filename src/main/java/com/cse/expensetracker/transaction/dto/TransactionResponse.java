package com.cse.expensetracker.transaction.dto;

import com.cse.expensetracker.transaction.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionResponse {
    private String id;
    private TransactionType type;
    private BigDecimal amount;
    private String category;
    private String note;
    private LocalDate date;
    private Instant createdAt;
    private Instant updatedAt;
}
