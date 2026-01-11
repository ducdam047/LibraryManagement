package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.models.RecordModel;
import com.example.librarymanagement.dtos.requests.action.BorrowBookRequest;
import com.example.librarymanagement.dtos.requests.action.ExtendBookRequest;
import com.example.librarymanagement.dtos.requests.action.ReturnBookRequest;
import com.example.librarymanagement.dtos.responses.api.ApiResponse;
import com.example.librarymanagement.services.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/borrowed")
public class RecordController {

    @Autowired
    private RecordService recordService;

    @GetMapping("/history")
    public ResponseEntity<List<RecordModel>> getRecordHistory() {
        List<RecordModel> recordModels = recordService.getRecordHistory();
        return ResponseEntity.ok(recordModels);
    }

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
    public ResponseEntity<ApiResponse<RecordModel>> borrowBook(@RequestBody BorrowBookRequest request) {
        ApiResponse<RecordModel> apiResponse = ApiResponse.<RecordModel>builder()
                .code(201)
                .message("Borrow request sent")
                .data(recordService.borrowBook(request))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PutMapping("/approve/{recordId}")
    public ResponseEntity<ApiResponse<RecordModel>> approveRecord(@PathVariable int recordId) {
        ApiResponse<RecordModel> apiResponse = ApiResponse.<RecordModel>builder()
                .code(200)
                .message("Approved successfully")
                .data(recordService.approveBorrow(recordId))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/reject/{recordId}")
    public ResponseEntity<ApiResponse<RecordModel>> rejectRecord(@PathVariable int recordId) {
        ApiResponse<RecordModel> apiResponse = ApiResponse.<RecordModel>builder()
                .code(200)
                .message("Rejected successfully")
                .data(recordService.rejectBorrow(recordId))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/return-book")
    public ResponseEntity<ApiResponse<String>> returnBook(@RequestBody ReturnBookRequest request) {
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(200)
                .message("return request sent")
                .data(recordService.returnBook(request))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/confirm/{recordId}")
    public ResponseEntity<ApiResponse<String>> confirmReturn(@PathVariable int recordId) {
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(200)
                .message("Confirm return successfully")
                .data(recordService.confirmReturn(recordId))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/extend-book")
    public ResponseEntity<ApiResponse<String>> extendBook(@RequestBody ExtendBookRequest request) {
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(200)
                .message("Extended successfully")
                .data(recordService.extendBook(request))
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
