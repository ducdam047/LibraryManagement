package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.models.EvaluateModel;
import com.example.librarymanagement.dtos.requests.action.EvaluateBookRequest;
import com.example.librarymanagement.dtos.responses.api.ApiResponse;
import com.example.librarymanagement.services.EvaluateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluate")
public class EvaluateController {

    @Autowired
    private EvaluateService evaluateService;

    @GetMapping("/exists")
    public ResponseEntity<?> checkEvaluated(@RequestParam String title) {
        return ResponseEntity.ok(evaluateService.checkEvaluated(title));
    }

    @GetMapping("/see-evaluated")
    public ApiResponse<List<EvaluateModel>> seeEvaluated(@RequestParam String title) {
        return ApiResponse.<List<EvaluateModel>>builder()
                .code(200)
                .message("Show evaluated successfully")
                .data(evaluateService.seeEvaluated(title))
                .build();
    }

    @PostMapping("/evaluate-book")
    public ApiResponse<EvaluateModel> evaluateBook(@RequestBody EvaluateBookRequest request) {
        return ApiResponse.<EvaluateModel>builder()
                .code(200)
                .message("Evaluated successfully")
                .data(evaluateService.evaluateBook(request))
                .build();
    }
}
