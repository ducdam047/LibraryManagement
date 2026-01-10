package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.models.ReadingModel;
import com.example.librarymanagement.dtos.requests.reading.ReadingAddRequest;
import com.example.librarymanagement.dtos.responses.api.ApiResponse;
import com.example.librarymanagement.entities.Reading;
import com.example.librarymanagement.services.ReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reading")
public class ReadingController {

    @Autowired
    private ReadingService readingService;

    @GetMapping
    public ResponseEntity<List<ReadingModel>> getReadingList() {
        List<ReadingModel> readingModels = readingService.getReadingList();
        return ResponseEntity.ok(readingModels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadingModel> getReadingBook(@PathVariable int id) {
        ReadingModel readingModel = readingService.getReadingBook(id);
        return ResponseEntity.ok(readingModel);
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<ReadingModel> getReadingBookId(@PathVariable int bookId) {
        ReadingModel readingModel = readingService.getReadingBookId(bookId);
        return ResponseEntity.ok(readingModel);
    }

    @PostMapping("/{bookId}")
    public ApiResponse<Reading> addToReading(@PathVariable int bookId) {
        Reading reading = readingService.addToReading(bookId);
        return ApiResponse.<Reading>builder()
                .code(201)
                .message("Book added to Reading successfully")
                .data(reading)
                .build();
    }

    @PostMapping()
    public ApiResponse<ReadingModel> saveReading(@RequestBody ReadingAddRequest request) {
        return ApiResponse.<ReadingModel>builder()
                .code(201)
                .message("Reading progress saved successfully")
                .data(readingService.saveReading(request))
                .build();
    }
}
