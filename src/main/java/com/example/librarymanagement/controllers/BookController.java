package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.models.BookModel;
import com.example.librarymanagement.dtos.models.BookTrending;
import com.example.librarymanagement.dtos.requests.book.BookAddRequest;
import com.example.librarymanagement.dtos.requests.book.BookUpdateRequest;
import com.example.librarymanagement.dtos.responses.api.ApiResponse;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.services.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/detail")
    public ApiResponse<BookModel> getBook(@RequestParam String title) {
        BookModel bookDetail = bookService.getBook(title);
        return ApiResponse.<BookModel>builder()
                .code(200)
                .message("Detail book")
                .data(bookDetail)
                .build();
    }

    @GetMapping("/featured")
    public ApiResponse<List<BookModel>> getFeaturedBooks() {
        List<BookModel> featuredBooks = bookService.getFeaturedBooks();
        return ApiResponse.<List<BookModel>>builder()
                .code(200)
                .message("Featured books")
                .data(featuredBooks)
                .build();
    }

    @GetMapping("/trending")
    public ApiResponse<List<BookTrending>> getTrendingBooks() {
        List<BookTrending> trendingBooks = bookService.getTrendingBooks(20);
        return ApiResponse.<List<BookTrending>>builder()
                .code(200)
                .message("Trending books")
                .data(trendingBooks)
                .build();
    }

    @GetMapping()
    public ApiResponse<List<BookModel>> filterCategory(@RequestParam(required = false) String categoryName) {
        List<BookModel> books = bookService.filterCategory(categoryName);
        return ApiResponse.<List<BookModel>>builder()
                .code(200)
                .message("Filtered category")
                .data(books)
                .build();
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<BookModel>> addBook(
            @RequestParam("bookData") String bookData,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam(value = "pdfFile", required = false) MultipartFile pdfFile) throws IOException {
        BookAddRequest request = new ObjectMapper().readValue(bookData, BookAddRequest.class);
        ApiResponse<BookModel> apiResponse = ApiResponse.<BookModel>builder()
                .code(201)
                .message("The book was added successfully")
                .data(bookService.addBook(request, imageFile, pdfFile))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<ApiResponse<Book>> updateBook(
            @PathVariable int bookId,
            @RequestParam("bookData") String bookData,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam(value = "pdfFile", required = false) MultipartFile pdfFile) throws IOException {
        BookUpdateRequest request = new ObjectMapper().readValue(bookData, BookUpdateRequest.class);
        ApiResponse<Book> apiResponse = ApiResponse.<Book>builder()
                .code(200)
                .message("Book update successfully")
                .data(bookService.updateBook(bookId, request, imageFile, pdfFile))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable int bookId) {
        bookService.deleteBook(bookId);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .code(204)
                .message("The book was deleted successfully")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
