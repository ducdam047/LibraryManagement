package com.example.librarymanagement.repositories;

import com.example.librarymanagement.entities.Reading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, Integer> {

    List<Reading> findByUser_UserId(int userId);
    Optional<Reading> findByBook_BookId(int bookId);
    Optional<Reading> findByUser_UserIdAndBook_BookId(int userId, int bookId);
}
