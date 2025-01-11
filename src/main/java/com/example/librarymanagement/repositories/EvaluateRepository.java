package com.example.librarymanagement.repositories;

import com.example.librarymanagement.entities.Evaluate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluateRepository extends JpaRepository<Evaluate, Integer> {

    List<Evaluate> findByUser_UserId(int userId);
    boolean existsByBook_BookId(int bookId);
}
