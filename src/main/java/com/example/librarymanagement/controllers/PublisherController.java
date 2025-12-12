package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.requests.publisher.PublisherAddRequest;
import com.example.librarymanagement.entities.Publisher;
import com.example.librarymanagement.services.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/publisher")
public class PublisherController {

    @Autowired
    private PublisherService publisherService;

    @GetMapping("/all")
    public ResponseEntity<List<Publisher>> getAllPublisher() {
        List<Publisher> publishers = publisherService.getAllPublisher();
        return ResponseEntity.ok(publishers);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addPublisher(@RequestBody PublisherAddRequest request) {
        Publisher publisher = publisherService.addPublisher(request);
        return ResponseEntity.ok(publisher);
    }
}
