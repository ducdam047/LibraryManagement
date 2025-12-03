package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.models.BookModel;
import com.example.librarymanagement.dtos.models.BorrowRecordModel;
import com.example.librarymanagement.dtos.models.EvaluateModel;
import com.example.librarymanagement.dtos.requests.action.BorrowBookRequest;
import com.example.librarymanagement.dtos.requests.action.EvaluateBookRequest;
import com.example.librarymanagement.dtos.requests.action.ExtendBookRequest;
import com.example.librarymanagement.dtos.requests.action.ReturnBookRequest;
import com.example.librarymanagement.dtos.responses.ApiResponse;
import com.example.librarymanagement.services.ActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/action")
public class LibraryActionController {

    @Autowired
    private ActionService actionService;

    @PostMapping("/return-book")
    public ApiResponse<String> returnBook(@RequestBody ReturnBookRequest request) {
        return ApiResponse.<String>builder()
                .code(200)
                .message("Returned successfully")
                .data(actionService.returnBook(request))
                .build();
    }

    @PostMapping("/evaluate-book")
    public ApiResponse<EvaluateModel> evaluateBook(@RequestBody EvaluateBookRequest request) {
        return ApiResponse.<EvaluateModel>builder()
                .code(200)
                .message("Evaluated successfully")
                .data(actionService.evaluateBook(request))
                .build();
    }

    @PostMapping("/extend-book")
    public ApiResponse<String> extendBook(@RequestBody ExtendBookRequest request) {
        return ApiResponse.<String>builder()
                .code(200)
                .message("Returned successfully")
                .data(actionService.extendBook(request))
                .build();
    }

    @GetMapping("/searchTitle/{title}")
    public ResponseEntity<BookModel> searchTitle(@PathVariable String title) {
        BookModel book = actionService.searchTitle(title);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/searchAuthor/{author}")
    public ResponseEntity<List<BookModel>> searchAuthor(@PathVariable String author) {
        List<BookModel> books = actionService.searchAuthor(author);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/searchPublisher/{publisherName}")
    public ResponseEntity<List<BookModel>> searchPublisher(@PathVariable String publisherName) {
        List<BookModel> books = actionService.searchPublisher(publisherName);
        return ResponseEntity.ok(books);
    }
}
