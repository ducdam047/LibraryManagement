package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.requests.category.CategoryAddRequest;
import com.example.librarymanagement.common.ApiResponse;
import com.example.librarymanagement.entities.Category;
import com.example.librarymanagement.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping()
    public ApiResponse<List<Category>> getAllCategory() {
        List<Category> categories = categoryService.getAllCategory();
        return ApiResponse.<List<Category>>builder()
                .code(200)
                .message("All categories")
                .data(categories)
                .build();
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<Category>> addCategory(@RequestBody CategoryAddRequest request) {
        Category category = categoryService.addCategory(request);
        ApiResponse<Category> apiResponse = ApiResponse.<Category>builder()
                .code(201)
                .message("Add category successfully")
                .data(category)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
}
