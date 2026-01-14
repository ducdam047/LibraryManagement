package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.models.LoanModel;
import com.example.librarymanagement.dtos.requests.action.BorrowBookRequest;
import com.example.librarymanagement.dtos.requests.action.ExtendBookRequest;
import com.example.librarymanagement.dtos.requests.action.ReturnBookRequest;
import com.example.librarymanagement.common.ApiResponse;
import com.example.librarymanagement.services.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/borrowed")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @GetMapping("/history")
    public ResponseEntity<List<LoanModel>> getLoanHistory() {
        List<LoanModel> loanModels = loanService.getLoanHistory();
        return ResponseEntity.ok(loanModels);
    }

    @GetMapping("/list-loan/{userId}")
    public ResponseEntity<List<LoanModel>> getLoanList(@PathVariable int userId) {
        List<LoanModel> loanModels = loanService.getLoanList(userId);
        return ResponseEntity.ok(loanModels);
    }

    @GetMapping("/list-loan-borrowed")
    public ResponseEntity<List<LoanModel>> getActiveOverdueLoanList() {
        List<LoanModel> loanModels = loanService.getActiveOverdueLoanList();
        return ResponseEntity.ok(loanModels);
    }

    @GetMapping("/list-loan-returned")
    public ResponseEntity<List<LoanModel>> getReturnedLoanList() {
        List<LoanModel> loanModels = loanService.getReturnedLoanList();
        return ResponseEntity.ok(loanModels);
    }

    @GetMapping("/loan-active/{bookId}")
    public ResponseEntity<LoanModel> getBorrowedBook(@PathVariable int bookId) {
        LoanModel loanModel = loanService.getBorrowedBook(bookId);
        return ResponseEntity.ok(loanModel);
    }

    @GetMapping("/loan-returned/{loanId}")
    public ResponseEntity<LoanModel> getReturnedBook(@PathVariable int loanId) {
        LoanModel loanModel = loanService.getReturnedBook(loanId);
        return ResponseEntity.ok(loanModel);
    }

    @PostMapping("/borrow-book")
    public ResponseEntity<ApiResponse<LoanModel>> borrowBook(@RequestBody BorrowBookRequest request) {
        ApiResponse<LoanModel> apiResponse = ApiResponse.<LoanModel>builder()
                .code(201)
                .message("Borrow request sent")
                .data(loanService.borrowBook(request))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PutMapping("/approve/{loanId}")
    public ResponseEntity<ApiResponse<LoanModel>> approveLoan(@PathVariable int loanId) {
        ApiResponse<LoanModel> apiResponse = ApiResponse.<LoanModel>builder()
                .code(200)
                .message("Approved successfully")
                .data(loanService.approveBorrow(loanId))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/reject/{loanId}")
    public ResponseEntity<ApiResponse<LoanModel>> rejectLoan(@PathVariable int loanId) {
        ApiResponse<LoanModel> apiResponse = ApiResponse.<LoanModel>builder()
                .code(200)
                .message("Rejected successfully")
                .data(loanService.rejectBorrow(loanId))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/return-book")
    public ResponseEntity<ApiResponse<String>> returnBook(@RequestBody ReturnBookRequest request) {
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(200)
                .message("return request sent")
                .data(loanService.returnBook(request))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/confirm/{loanId}")
    public ResponseEntity<ApiResponse<String>> confirmReturn(@PathVariable int loanId) {
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(200)
                .message("Confirm return successfully")
                .data(loanService.confirmReturn(loanId))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/extend-book")
    public ResponseEntity<ApiResponse<String>> extendBook(@RequestBody ExtendBookRequest request) {
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(200)
                .message("Extended successfully")
                .data(loanService.extendBook(request))
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
