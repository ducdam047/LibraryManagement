package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.requests.category.CategoryAddRequest;
import com.example.librarymanagement.dtos.responses.api.ApiResponse;
import com.example.librarymanagement.entities.Category;
import com.example.librarymanagement.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

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
    public ApiResponse<Category> addCategory(@RequestBody CategoryAddRequest request) {
        Category category = categoryService.addCategory(request);
        return ApiResponse.<Category>builder()
                .code(200)
                .message("Add category successfully")
                .data(category)
                .build();
    }
}
