package com.example.librarymanagement.repositories;

import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.Record;
import com.example.librarymanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record, Integer> {

    Optional<Record> findByBook(Book book);
    Optional<Record> findByUserAndBookAndStatus(User user, Book book, String status);
    Optional<Record> findFirstByUserAndBookAndStatusOrderByReturnedDayDesc(User user, Book book, String status);
    Optional<Record> findByUserAndBook_Title(User user, String title);
    boolean existsByUserAndBook_TitleAndStatus(User user, String title, String status);
    Optional<Record> findByBookAndStatus(Book book, String status);
    List<Record> findByStatusAndDueDayBefore(String status, LocalDate currentDate);
    List<Record> findByUser_UserIdAndStatus(int userId, String status);
}
