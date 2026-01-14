package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.models.LoanModel;
import com.example.librarymanagement.common.ApiResponse;
import com.example.librarymanagement.services.PendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pending")
public class PendingController {

    @Autowired
    private PendingService pendingService;

    @GetMapping("/borrow")
    public ApiResponse<List<LoanModel>> getLoanPendingBorrow() {
        return ApiResponse.<List<LoanModel>>builder()
                .code(200)
                .message("Show book pending borrow successfully")
                .data(pendingService.getLoanPendingBorrow())
                .build();
    }

    @GetMapping("/return")
    public ApiResponse<List<LoanModel>> getLoanPendingReturn() {
        return ApiResponse.<List<LoanModel>>builder()
                .code(200)
                .message("Show book pending return successfully")
                .data(pendingService.getLoanPendingReturn())
                .build();
    }

    @PutMapping("/cancel/{loanId}")
    public ResponseEntity<ApiResponse<String>> cancelPendingBorrow(@PathVariable int loanId) {
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(200)
                .message("Loan application cancelled successfully")
                .data(pendingService.cancelPendingBorrow(loanId))
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
