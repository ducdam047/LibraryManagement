package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.requests.category.CategoryAddRequest;
import com.example.librarymanagement.entities.Category;
import com.example.librarymanagement.enums.ErrorCode;
import com.example.librarymanagement.exception.AppException;
import com.example.librarymanagement.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Cacheable("categories:all")
    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Category addCategory(CategoryAddRequest request) {
        if(categoryRepository.existsByCategoryId(request.getCategoryId()))
            throw new AppException(ErrorCode.CATEGORY_ID_EXISTS);
        if(categoryRepository.existsByCategoryNameIgnoreCase(request.getCategoryName()))
            throw new AppException(ErrorCode.CATEGORY_NAME_EXISTS);

        Category category = new Category();
        category.setCategoryId(request.getCategoryId());
        category.setCategoryName(request.getCategoryName());

        return categoryRepository.save(category);
    }
}
