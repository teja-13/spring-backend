package com.cse.expensetracker.analytics.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonthlySummaryItem {
    private String month;
    private BigDecimal income;
    private BigDecimal expense;
}
