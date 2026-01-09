package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.requests.publisher.PublisherAddRequest;
import com.example.librarymanagement.dtos.responses.api.ApiResponse;
import com.example.librarymanagement.entities.Publisher;
import com.example.librarymanagement.services.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/publishers")
public class PublisherController {

    @Autowired
    private PublisherService publisherService;

    @GetMapping()
    public ApiResponse<List<Publisher>> getAllPublisher() {
        List<Publisher> publishers = publisherService.getAllPublisher();
        return ApiResponse.<List<Publisher>>builder()
                .code(200)
                .message("All publishers")
                .data(publishers)
                .build();
    }

    @PostMapping()
    public ApiResponse<Publisher> addPublisher(@RequestBody PublisherAddRequest request) {
        Publisher publisher = publisherService.addPublisher(request);
        return ApiResponse.<Publisher>builder()
                .code(200)
                .message("Add publisher successfully")
                .data(publisher)
                .build();
    }
}
