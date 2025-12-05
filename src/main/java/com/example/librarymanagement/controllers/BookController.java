package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.models.BookModel;
import com.example.librarymanagement.dtos.requests.book.BookAddRequest;
import com.example.librarymanagement.dtos.responses.ApiResponse;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.services.ActionService;
import com.example.librarymanagement.services.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private ActionService actionService;

    @GetMapping("/{bookId}")
    public ResponseEntity<BookModel> getBook(@PathVariable int bookId) {
        BookModel bookModel = bookService.getBook(bookId);
        return ResponseEntity.ok(bookModel);
    }

    @GetMapping("/featured")
    public ResponseEntity<List<BookModel>> getFeaturedBooks() {
        List<BookModel> featuredBooks = bookService.getFeaturedBooks();
        return ResponseEntity.ok(featuredBooks);
    }

    @GetMapping("/trending")
    public ResponseEntity<List<BookModel>> getTrendingBooks() {
        List<BookModel> trendingBooks = bookService.getTrendingBooks(20);
        return ResponseEntity.ok(trendingBooks);
    }

    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<BookModel>> filterCategory(@PathVariable String categoryName) {
        List<BookModel> books = actionService.filterCategory(categoryName);
        return ResponseEntity.ok(books);
    }

    @PostMapping("/add-book")
    public ApiResponse<BookModel> addBook(
            @RequestParam("bookData") String bookData,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam(value = "pdfFile", required = false) MultipartFile pdfFile) throws IOException {
        BookAddRequest request = new ObjectMapper().readValue(bookData, BookAddRequest.class);
        return ApiResponse.<BookModel>builder()
                .code(201)
                .message("The book was added successfully")
                .data(bookService.addBook(request, imageFile, pdfFile))
                .build();
    }

    @DeleteMapping("/delete-book/{bookId}")
    public ApiResponse<String> deleteBook(@PathVariable int bookId) {
        bookService.deleteBook(bookId);
        return ApiResponse.<String>builder()
                .code(204)
                .message("The book was deleted successfully")
                .data("Book with ID " + bookId + " has been deleted")
                .build();
    }
}
