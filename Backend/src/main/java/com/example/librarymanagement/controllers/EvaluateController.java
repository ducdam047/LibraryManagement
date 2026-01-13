package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.models.EvaluateModel;
import com.example.librarymanagement.dtos.requests.action.EvaluateBookRequest;
import com.example.librarymanagement.common.ApiResponse;
import com.example.librarymanagement.dtos.responses.rating.RatingCountResponse;
import com.example.librarymanagement.services.EvaluateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluations")
public class EvaluateController {

    @Autowired
    private EvaluateService evaluateService;

    @GetMapping("/status")
    public ApiResponse<?> checkEvaluated(@RequestParam String title) {
        return ApiResponse.builder()
                .code(200)
                .message("")
                .data(evaluateService.checkEvaluated(title))
                .build();
    }

    @GetMapping()
    public ApiResponse<List<EvaluateModel>> seeEvaluated(@RequestParam String title) {
        return ApiResponse.<List<EvaluateModel>>builder()
                .code(200)
                .message("Show evaluated successfully")
                .data(evaluateService.seeEvaluated(title))
                .build();
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<EvaluateModel>> evaluateBook(@RequestBody EvaluateBookRequest request) {
        ApiResponse<EvaluateModel> apiResponse = ApiResponse.<EvaluateModel>builder()
                .code(200)
                .message("Evaluated successfully")
                .data(evaluateService.evaluateBook(request))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/ratings")
    public ApiResponse<List<RatingCountResponse>> countRating(@RequestParam String title) {
        return ApiResponse.<List<RatingCountResponse>>builder()
                .code(200)
                .message("Get rating count successfully")
                .data(evaluateService.countRating(title))
                .build();
    }

    @GetMapping("/ratings/average")
    public ApiResponse<Double> averageRating(@RequestParam String title) {
        return ApiResponse.<Double>builder()
                .code(200)
                .message("Get average rating successfully")
                .data(evaluateService.averageRating(title))
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
