package com.example.librarymanagement.controllers;

import com.example.librarymanagement.services.EvaluateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/evaluate")
public class EvaluateController {

    @Autowired
    private EvaluateService evaluateService;

    @GetMapping("/exists")
    public ResponseEntity<?> checkEvaluated(@RequestParam String title) {
        return ResponseEntity.ok(evaluateService.checkEvaluated(title));
    }
}
