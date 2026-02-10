package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.models.BookModel;
import com.example.librarymanagement.services.ActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LibraryActionController {

    private final ActionService actionService;

    @GetMapping("/searchTitle/{title}")
    public ResponseEntity<BookModel> searchTitle(@PathVariable String title) {
        BookModel book = actionService.searchTitle(title);
        return ResponseEntity.ok(book);
    }
}
