package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.models.ReadingModel;
import com.example.librarymanagement.dtos.requests.reading.ReadingAddRequest;
import com.example.librarymanagement.dtos.responses.ApiResponse;
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

    @PostMapping("/save")
    public ApiResponse<ReadingModel> saveReading(@RequestBody ReadingAddRequest request) {
        return ApiResponse.<ReadingModel>builder()
                .code(201)
                .message("Reading progress saved successfully")
                .data(readingService.saveReading(request))
                .build();
    }
}
