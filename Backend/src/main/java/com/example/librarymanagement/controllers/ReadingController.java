package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.models.ReadingModel;
import com.example.librarymanagement.dtos.requests.reading.ReadingAddRequest;
import com.example.librarymanagement.common.ApiResponse;
import com.example.librarymanagement.entities.Reading;
import com.example.librarymanagement.services.ReadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reading")
@RequiredArgsConstructor
public class ReadingController {

    private final ReadingService readingService;

    @GetMapping
    public ApiResponse<List<ReadingModel>> getReadingList() {
        List<ReadingModel> readingModels = readingService.getReadingList();
        return ApiResponse.<List<ReadingModel>>builder()
                .code(200)
                .message("List book reading")
                .data(readingModels)
                .build();
    }

    @GetMapping("/{readingId}")
    public ApiResponse<ReadingModel> getReadingBook(@PathVariable int readingId) {
        ReadingModel readingModel = readingService.getReadingBook(readingId);
        return ApiResponse.<ReadingModel>builder()
                .code(200)
                .message("Book Reading")
                .data(readingModel)
                .build();
    }

    @GetMapping("/book/{bookId}")
    public ApiResponse<ReadingModel> getReadingBookId(@PathVariable int bookId) {
        ReadingModel readingModel = readingService.getReadingBookId(bookId);
        return ApiResponse.<ReadingModel>builder()
                .code(200)
                .message("Book Reading")
                .data(readingModel)
                .build();
    }

    @PostMapping("/{bookId}")
    public ResponseEntity<ApiResponse<Reading>> addToReading(@PathVariable int bookId) {
        Reading reading = readingService.addToReading(bookId);
        ApiResponse<Reading> apiResponse = ApiResponse.<Reading>builder()
                .code(201)
                .message("Book added to Reading successfully")
                .data(reading)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/progress")
    public ResponseEntity<ApiResponse<ReadingModel>> saveReading(@RequestBody ReadingAddRequest request) {
        ApiResponse<ReadingModel> apiResponse = ApiResponse.<ReadingModel>builder()
                .code(201)
                .message("Reading progress saved successfully")
                .data(readingService.saveReading(request))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
}
