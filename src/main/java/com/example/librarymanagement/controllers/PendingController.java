package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.models.BorrowOrderModel;
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
    public ApiResponse<List<BorrowOrderModel>> getRecordPendingBorrow() {
        return ApiResponse.<List<BorrowOrderModel>>builder()
                .code(200)
                .message("Show book pending borrow successfully")
                .data(pendingService.getRecordPendingBorrow())
                .build();
    }

    @GetMapping("/return")
    public ApiResponse<List<BorrowOrderModel>> getRecordPendingReturn() {
        return ApiResponse.<List<BorrowOrderModel>>builder()
                .code(200)
                .message("Show book pending return successfully")
                .data(pendingService.getRecordPendingReturn())
                .build();
    }

    @PutMapping("/cancel/{recordId}")
    public ResponseEntity<ApiResponse<String>> cancelPendingBorrow(@PathVariable int recordId) {
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(200)
                .message("Record application cancelled successfully")
                .data(pendingService.cancelPendingBorrow(recordId))
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
