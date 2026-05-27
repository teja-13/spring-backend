package com.cse.expensetracker.transaction;

import com.cse.expensetracker.common.ApiResponse;
import com.cse.expensetracker.common.PagedResponse;
import com.cse.expensetracker.transaction.dto.TransactionRequest;
import com.cse.expensetracker.transaction.dto.TransactionResponse;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TransactionRequest request
    ) {
        TransactionResponse response = transactionService.create(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.<TransactionResponse>builder()
                .success(true)
                .message("Transaction created")
                .data(response)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id
    ) {
        TransactionResponse response = transactionService.getById(userDetails.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.<TransactionResponse>builder()
                .success(true)
                .message("Transaction fetched")
                .data(response)
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> update(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id,
            @Valid @RequestBody TransactionRequest request
    ) {
        TransactionResponse response = transactionService.update(userDetails.getUsername(), id, request);
        return ResponseEntity.ok(ApiResponse.<TransactionResponse>builder()
                .success(true)
                .message("Transaction updated")
                .data(response)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id
    ) {
        transactionService.delete(userDetails.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Transaction deleted")
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<TransactionResponse>>> list(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir
    ) {
        PagedResponse<TransactionResponse> response = transactionService.list(
                userDetails.getUsername(), search, category, type, from, to, page, size, sortBy, sortDir
        );
        return ResponseEntity.ok(ApiResponse.<PagedResponse<TransactionResponse>>builder()
                .success(true)
                .message("Transactions fetched")
                .data(response)
                .build());
    }
}
