package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.models.BorrowOrderModel;
import com.example.librarymanagement.dtos.requests.action.BorrowBookRequest;
import com.example.librarymanagement.dtos.requests.action.ExtendBookRequest;
import com.example.librarymanagement.dtos.requests.action.ReturnBookRequest;
import com.example.librarymanagement.common.ApiResponse;
import com.example.librarymanagement.services.BorrowOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/borrowed")
public class BorrowOrderController {

    @Autowired
    private BorrowOrderService borrowOrderService;

    @GetMapping("/history")
    public ResponseEntity<List<BorrowOrderModel>> getRecordHistory() {
        List<BorrowOrderModel> borrowOrderModels = borrowOrderService.getRecordHistory();
        return ResponseEntity.ok(borrowOrderModels);
    }

    @GetMapping("/list-record/{userId}")
    public ResponseEntity<List<BorrowOrderModel>> getRecordList(@PathVariable int userId) {
        List<BorrowOrderModel> borrowOrderModels = borrowOrderService.getRecordList(userId);
        return ResponseEntity.ok(borrowOrderModels);
    }

    @GetMapping("/list-record-borrowed")
    public ResponseEntity<List<BorrowOrderModel>> getActiveOverdueRecordList() {
        List<BorrowOrderModel> borrowOrderModels = borrowOrderService.getActiveOverdueRecordList();
        return ResponseEntity.ok(borrowOrderModels);
    }

    @GetMapping("/list-record-returned")
    public ResponseEntity<List<BorrowOrderModel>> getReturnedRecordList() {
        List<BorrowOrderModel> borrowOrderModels = borrowOrderService.getReturnedRecordList();
        return ResponseEntity.ok(borrowOrderModels);
    }

    @GetMapping("/record-active/{bookId}")
    public ResponseEntity<BorrowOrderModel> getBorrowedBook(@PathVariable int bookId) {
        BorrowOrderModel borrowOrderModel = borrowOrderService.getBorrowedBook(bookId);
        return ResponseEntity.ok(borrowOrderModel);
    }

    @GetMapping("/record-returned/{recordId}")
    public ResponseEntity<BorrowOrderModel> getReturnedBook(@PathVariable int recordId) {
        BorrowOrderModel borrowOrderModel = borrowOrderService.getReturnedBook(recordId);
        return ResponseEntity.ok(borrowOrderModel);
    }

    @PostMapping("/borrow-book")
    public ResponseEntity<ApiResponse<BorrowOrderModel>> borrowBook(@RequestBody BorrowBookRequest request) {
        ApiResponse<BorrowOrderModel> apiResponse = ApiResponse.<BorrowOrderModel>builder()
                .code(201)
                .message("Borrow request sent")
                .data(borrowOrderService.borrowBook(request))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PutMapping("/approve/{recordId}")
    public ResponseEntity<ApiResponse<BorrowOrderModel>> approveRecord(@PathVariable int recordId) {
        ApiResponse<BorrowOrderModel> apiResponse = ApiResponse.<BorrowOrderModel>builder()
                .code(200)
                .message("Approved successfully")
                .data(borrowOrderService.approveBorrow(recordId))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/reject/{recordId}")
    public ResponseEntity<ApiResponse<BorrowOrderModel>> rejectRecord(@PathVariable int recordId) {
        ApiResponse<BorrowOrderModel> apiResponse = ApiResponse.<BorrowOrderModel>builder()
                .code(200)
                .message("Rejected successfully")
                .data(borrowOrderService.rejectBorrow(recordId))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/return-book")
    public ResponseEntity<ApiResponse<String>> returnBook(@RequestBody ReturnBookRequest request) {
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(200)
                .message("return request sent")
                .data(borrowOrderService.returnBook(request))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/confirm/{recordId}")
    public ResponseEntity<ApiResponse<String>> confirmReturn(@PathVariable int recordId) {
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(200)
                .message("Confirm return successfully")
                .data(borrowOrderService.confirmReturn(recordId))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/extend-book")
    public ResponseEntity<ApiResponse<String>> extendBook(@RequestBody ExtendBookRequest request) {
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(200)
                .message("Extended successfully")
                .data(borrowOrderService.extendBook(request))
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
