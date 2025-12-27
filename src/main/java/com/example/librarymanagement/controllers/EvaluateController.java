package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.models.EvaluateModel;
import com.example.librarymanagement.dtos.requests.action.EvaluateBookRequest;
import com.example.librarymanagement.dtos.responses.api.ApiResponse;
import com.example.librarymanagement.dtos.responses.rating.RatingCountResponse;
import com.example.librarymanagement.dtos.responses.rating.RatingSummaryResponse;
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
    public ResponseEntity<?> checkEvaluated(@RequestParam int bookId) {
        return ResponseEntity.ok(evaluateService.checkEvaluated(bookId));
    }

    @GetMapping("/review-evaluated")
    public ApiResponse<List<EvaluateModel>> seeEvaluated(@RequestParam int bookId) {
        return ApiResponse.<List<EvaluateModel>>builder()
                .code(200)
                .message("Show evaluated successfully")
                .data(evaluateService.seeEvaluated(bookId))
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

    @GetMapping("/rating-count")
    public ApiResponse<List<RatingCountResponse>> countRating(@RequestParam int bookId) {
        return ApiResponse.<List<RatingCountResponse>>builder()
                .code(200)
                .message("Get rating count successfully")
                .data(evaluateService.countRating(bookId))
                .build();
    }

    @GetMapping("/average")
    public ApiResponse<Double> averageRating(@RequestParam int bookId) {
        return ApiResponse.<Double>builder()
                .code(200)
                .message("Get average rating successfully")
                .data(evaluateService.averageRating(bookId))
                .build();
    }

//    @GetMapping("/average-test")
//    public ApiResponse<RatingSummaryResponse> getRatingSummary(@RequestParam String title) {
//        return ApiResponse.<RatingSummaryResponse>builder()
//                .code(200)
//                .message("Get average and total rating successfully")
//                .data(evaluateService.getRatingSummary(title))
//                .build();
//    }
}
