package com.cse.expensetracker.analytics;

import com.cse.expensetracker.analytics.dto.CategorySummaryItem;
import com.cse.expensetracker.analytics.dto.MonthlySummaryItem;
import com.cse.expensetracker.analytics.dto.SummaryResponse;
import com.cse.expensetracker.common.ApiResponse;
import com.cse.expensetracker.transaction.TransactionType;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<SummaryResponse>> summary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        SummaryResponse response = analyticsService.summary(userDetails.getUsername(), from, to);
        return ResponseEntity.ok(ApiResponse.<SummaryResponse>builder()
                .success(true)
                .message("Summary fetched")
                .data(response)
                .build());
    }

    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<List<MonthlySummaryItem>>> monthly(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        List<MonthlySummaryItem> response = analyticsService.monthly(userDetails.getUsername(), from, to);
        return ResponseEntity.ok(ApiResponse.<List<MonthlySummaryItem>>builder()
                .success(true)
                .message("Monthly analytics fetched")
                .data(response)
                .build());
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategorySummaryItem>>> categories(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam TransactionType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        List<CategorySummaryItem> response = analyticsService.categories(userDetails.getUsername(), from, to, type);
        return ResponseEntity.ok(ApiResponse.<List<CategorySummaryItem>>builder()
                .success(true)
                .message("Category analytics fetched")
                .data(response)
                .build());
    }
}
