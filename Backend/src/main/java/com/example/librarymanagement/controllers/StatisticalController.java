package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.models.BookModel;
import com.example.librarymanagement.common.ApiResponse;
import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.services.StatisticalService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/statistical")
@RequiredArgsConstructor
public class StatisticalController {

    private final StatisticalService statisticalService;

    @GetMapping("/all-book")
    public long allBook() {
        return statisticalService.countAllBook();
    }

    @GetMapping("/book-available")
    public long bookAvailable() {
        return statisticalService.countBookAvailable();
    }

    @GetMapping("/book-count-borrowing")
    public long bookCountBorrowing() {
        return statisticalService.countBookBorrowing();
    }

    @GetMapping("/book-borrowing")
    public ApiResponse<List<BookModel>> bookBorrowing() {
        List<BookModel> bookModels = statisticalService.bookBorrowing();
        return ApiResponse.<List<BookModel>>builder()
                .code(200)
                .message("There are " + statisticalService.countBookBorrowing() + " books being borrowed")
                .data(bookModels)
                .build();
    }

    @GetMapping("/user-count-locked")
    public long userCountLocked() {
        return statisticalService.countUserLocked();
    }

    @GetMapping("/user-locked")
    public ApiResponse<List<User>> userLocked() {
        List<User> users = statisticalService.userLocked();
        return ApiResponse.<List<User>>builder()
                .code(200)
                .message("There are " + statisticalService.countUserLocked() + " users being locked")
                .data(users)
                .build();
    }
}
