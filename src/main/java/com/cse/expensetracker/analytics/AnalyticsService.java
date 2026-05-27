package com.cse.expensetracker.analytics;

import com.cse.expensetracker.analytics.dto.CategorySummaryItem;
import com.cse.expensetracker.analytics.dto.MonthlySummaryItem;
import com.cse.expensetracker.analytics.dto.SummaryResponse;
import com.cse.expensetracker.transaction.Transaction;
import com.cse.expensetracker.transaction.TransactionType;
import com.cse.expensetracker.user.User;
import com.cse.expensetracker.user.UserService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final MongoTemplate mongoTemplate;
    private final UserService userService;

    public SummaryResponse summary(String email, LocalDate from, LocalDate to) {
        List<Transaction> transactions = fetchTransactions(email, from, to);
        BigDecimal income = totalFor(transactions, TransactionType.INCOME);
        BigDecimal expense = totalFor(transactions, TransactionType.EXPENSE);
        return SummaryResponse.builder()
                .totalIncome(income)
                .totalExpense(expense)
                .balance(income.subtract(expense))
                .build();
    }

    public List<MonthlySummaryItem> monthly(String email, LocalDate from, LocalDate to) {
        List<Transaction> transactions = fetchTransactions(email, from, to);
        Map<YearMonth, List<Transaction>> grouped = transactions.stream()
                .collect(Collectors.groupingBy(t -> YearMonth.from(t.getDate())));
        List<MonthlySummaryItem> items = new ArrayList<>();
        for (Map.Entry<YearMonth, List<Transaction>> entry : grouped.entrySet()) {
            BigDecimal income = totalFor(entry.getValue(), TransactionType.INCOME);
            BigDecimal expense = totalFor(entry.getValue(), TransactionType.EXPENSE);
            items.add(MonthlySummaryItem.builder()
                    .month(entry.getKey().toString())
                    .income(income)
                    .expense(expense)
                    .build());
        }
        items.sort(Comparator.comparing(MonthlySummaryItem::getMonth));
        return items;
    }

    public List<CategorySummaryItem> categories(String email, LocalDate from, LocalDate to, TransactionType type) {
        List<Transaction> transactions = fetchTransactions(email, from, to).stream()
                .filter(t -> t.getType() == type)
                .collect(Collectors.toList());

        Map<String, BigDecimal> totals = new HashMap<>();
        for (Transaction transaction : transactions) {
            totals.put(
                    transaction.getCategory(),
                    totals.getOrDefault(transaction.getCategory(), BigDecimal.ZERO)
                            .add(transaction.getAmount())
            );
        }
        return totals.entrySet().stream()
                .map(entry -> CategorySummaryItem.builder()
                        .category(entry.getKey())
                        .total(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(CategorySummaryItem::getTotal).reversed())
                .collect(Collectors.toList());
    }

    private List<Transaction> fetchTransactions(String email, LocalDate from, LocalDate to) {
        User user = userService.getByEmail(email);
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(user.getId()));
        if (from != null && to != null) {
            query.addCriteria(Criteria.where("date").gte(from).lte(to));
        } else if (from != null) {
            query.addCriteria(Criteria.where("date").gte(from));
        } else if (to != null) {
            query.addCriteria(Criteria.where("date").lte(to));
        }
        return mongoTemplate.find(query, Transaction.class);
    }

    private BigDecimal totalFor(List<Transaction> transactions, TransactionType type) {
        return transactions.stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
