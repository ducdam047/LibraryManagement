package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.models.RecordModel;
import com.example.librarymanagement.dtos.requests.action.BorrowBookRequest;
import com.example.librarymanagement.dtos.requests.action.ExtendBookRequest;
import com.example.librarymanagement.dtos.requests.action.ReturnBookRequest;
import com.example.librarymanagement.dtos.responses.api.ApiResponse;
import com.example.librarymanagement.services.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/borrowed")
public class RecordController {

    @Autowired
    private RecordService recordService;

    @GetMapping("/list-record/{userId}")
    public ResponseEntity<List<RecordModel>> getRecordList(@PathVariable int userId) {
        List<RecordModel> recordModels = recordService.getRecordList(userId);
        return ResponseEntity.ok(recordModels);
    }

    @GetMapping("/list-record-borrowed")
    public ResponseEntity<List<RecordModel>> getActiveOverdueRecordList() {
        List<RecordModel> recordModels = recordService.getActiveOverdueRecordList();
        return ResponseEntity.ok(recordModels);
    }

    @GetMapping("/list-record-returned")
    public ResponseEntity<List<RecordModel>> getReturnedRecordList() {
        List<RecordModel> recordModels = recordService.getReturnedRecordList();
        return ResponseEntity.ok(recordModels);
    }

    @GetMapping("/record-active/{bookId}")
    public ResponseEntity<RecordModel> getBorrowedBook(@PathVariable int bookId) {
        RecordModel recordModel = recordService.getBorrowedBook(bookId);
        return ResponseEntity.ok(recordModel);
    }

    @GetMapping("/record-returned/{recordId}")
    public ResponseEntity<RecordModel> getReturnedBook(@PathVariable int recordId) {
        RecordModel recordModel = recordService.getReturnedBook(recordId);
        return ResponseEntity.ok(recordModel);
    }

    @PostMapping("/borrow-book")
    public ApiResponse<RecordModel> borrowBook(@RequestBody BorrowBookRequest request) {
        return ApiResponse.<RecordModel>builder()
                .code(200)
                .message("Borrowed successfully")
                .data(recordService.borrowBook(request))
                .build();
    }

    @PutMapping("/approve/{recordId}")
    public ApiResponse<RecordModel> approveRecord(@PathVariable int recordId) {
        return ApiResponse.<RecordModel>builder()
                .code(200)
                .message("Approved successfully")
                .data(recordService.approveBorrow(recordId))
                .build();
    }

    @PutMapping("/reject/{recordId}")
    public ApiResponse<RecordModel> rejectRecord(@PathVariable int recordId) {
        return ApiResponse.<RecordModel>builder()
                .code(200)
                .message("Rejected successfully")
                .data(recordService.rejectBorrow(recordId))
                .build();
    }

    @PostMapping("/return-book")
    public ApiResponse<String> returnBook(@RequestBody ReturnBookRequest request) {
        return ApiResponse.<String>builder()
                .code(200)
                .message("Returned successfully")
                .data(recordService.returnBook(request))
                .build();
    }

    @PostMapping("/extend-book")
    public ApiResponse<String> extendBook(@RequestBody ExtendBookRequest request) {
        return ApiResponse.<String>builder()
                .code(200)
                .message("Extended successfully")
                .data(recordService.extendBook(request))
                .build();
    }
}
