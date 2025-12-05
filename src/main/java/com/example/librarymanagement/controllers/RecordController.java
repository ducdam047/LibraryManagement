package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.models.BookModel;
import com.example.librarymanagement.dtos.models.RecordModel;
import com.example.librarymanagement.dtos.models.EvaluateModel;
import com.example.librarymanagement.dtos.requests.action.BorrowBookRequest;
import com.example.librarymanagement.dtos.requests.action.EvaluateBookRequest;
import com.example.librarymanagement.dtos.requests.action.ExtendBookRequest;
import com.example.librarymanagement.dtos.requests.action.ReturnBookRequest;
import com.example.librarymanagement.dtos.responses.ApiResponse;
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

    @GetMapping("/active")
    public ResponseEntity<List<BookModel>> getBorrowedBookList() {
        List<BookModel> bookModels = recordService.getBorrowedBookList();
        return ResponseEntity.ok(bookModels);
    }

    @GetMapping("/list-record-returned")
    public ResponseEntity<List<RecordModel>> getReturnedBookList() {
        List<RecordModel> bookModels = recordService.getReturnedBookList();
        return ResponseEntity.ok(bookModels);
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
