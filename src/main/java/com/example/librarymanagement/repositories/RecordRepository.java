package com.example.librarymanagement.repositories;

import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.BorrowRecord;
import com.example.librarymanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RecordRepository extends JpaRepository<BorrowRecord, Integer> {

    Optional<BorrowRecord> findByBook(Book book);
    Optional<BorrowRecord> findByUserAndBookAndStatus(User user, Book book, String status);
    Optional<BorrowRecord> findByUserAndBook_Title(User user, String title);
    boolean existsByUserAndBook_TitleAndStatus(User user, String title, String status);
    Optional<BorrowRecord> findByBookAndStatus(Book book, String status);
    List<BorrowRecord> findByStatusAndDueDayBefore(String status, LocalDate currentDate);
    List<BorrowRecord> findByUser_UserIdAndStatus(int userId, String status);
}
