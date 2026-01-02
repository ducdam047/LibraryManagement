package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.models.RecordModel;
import com.example.librarymanagement.dtos.responses.api.ApiResponse;
import com.example.librarymanagement.services.PendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pending")
public class PendingController {

    @Autowired
    private PendingService pendingService;

    @GetMapping("/borrow")
    public ApiResponse<List<RecordModel>> getRecordPendingBorrow() {
        return ApiResponse.<List<RecordModel>>builder()
                .code(200)
                .message("Show book pending borrow successfully")
                .data(pendingService.getRecordPendingBorrow())
                .build();
    }

    @GetMapping("/return")
    public ApiResponse<List<RecordModel>> getRecordPendingReturn() {
        return ApiResponse.<List<RecordModel>>builder()
                .code(200)
                .message("Show book pending return successfully")
                .data(pendingService.getRecordPendingReturn())
                .build();
    }
}
