package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.models.BookModel;
import com.example.librarymanagement.dtos.models.BorrowRecordModel;
import com.example.librarymanagement.dtos.models.EvaluateModel;
import com.example.librarymanagement.dtos.requests.action.BorrowBookRequest;
import com.example.librarymanagement.dtos.requests.action.EvaluateBookRequest;
import com.example.librarymanagement.dtos.requests.action.ExtendBookRequest;
import com.example.librarymanagement.dtos.requests.action.ReturnBookRequest;
import com.example.librarymanagement.dtos.responses.ApiResponse;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.services.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/borrowed")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    @GetMapping
    public ResponseEntity<List<BookModel>> getBorrowedBookList() {
        List<BookModel> bookModels = borrowService.getBorrowedBookList();
        return ResponseEntity.ok(bookModels);
    }

    @GetMapping("/record/{bookId}")
    public ResponseEntity<BorrowRecordModel> getBorrowedBook(@PathVariable int bookId) {
        BorrowRecordModel recordModel = borrowService.getBorrowedBook(bookId);
        return ResponseEntity.ok(recordModel);
    }

    @PostMapping("/borrow-book")
    public ApiResponse<BorrowRecordModel> borrowBook(@RequestBody BorrowBookRequest request) {
        return ApiResponse.<BorrowRecordModel>builder()
                .code(200)
                .message("Borrowed successfully")
                .data(borrowService.borrowBook(request))
                .build();
    }

    @PostMapping("/return-book")
    public ApiResponse<String> returnBook(@RequestBody ReturnBookRequest request) {
        return ApiResponse.<String>builder()
                .code(200)
                .message("Returned successfully")
                .data(borrowService.returnBook(request))
                .build();
    }

    @PostMapping("/extend-book")
    public ApiResponse<String> extendBook(@RequestBody ExtendBookRequest request) {
        return ApiResponse.<String>builder()
                .code(200)
                .message("Extended successfully")
                .data(borrowService.extendBook(request))
                .build();
    }

    @PostMapping("/evaluate-book")
    public ApiResponse<EvaluateModel> evaluateBook(@RequestBody EvaluateBookRequest request) {
        return ApiResponse.<EvaluateModel>builder()
                .code(200)
                .message("Evaluated successfully")
                .data(borrowService.evaluateBook(request))
                .build();
    }
}
