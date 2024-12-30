package com.example.librarymanagement.repositories;

import com.example.librarymanagement.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {

    Optional<Category> findByCategoryName(String categoryName);
}
