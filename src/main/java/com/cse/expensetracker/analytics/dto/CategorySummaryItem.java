package com.cse.expensetracker.analytics.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategorySummaryItem {
    private String category;
    private BigDecimal total;
}
