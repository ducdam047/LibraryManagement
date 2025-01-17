package com.example.librarymanagement.repositories;

import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.BorrowRecord;
import com.example.librarymanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Integer> {

    Optional<BorrowRecord> findByBook(Book book);
    boolean existsByBook_Title(String title);
    Optional<BorrowRecord> findByBookAndStatus(Book book, String status);
    List<BorrowRecord> findByStatusAndDueDayBefore(String status, LocalDate currentDate);
}
