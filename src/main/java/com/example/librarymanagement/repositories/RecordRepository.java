package com.example.librarymanagement.repositories;

import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.Record;
import com.example.librarymanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record, Integer> {

    Optional<Record> findByBook(Book book);
    Optional<Record> findByUserAndBookAndStatus(User user, Book book, String status);
    Optional<Record> findByUserAndBookAndStatusIn(User user, Book book, List<String> statusList);
    Optional<Record> findFirstByUserAndBookAndStatusOrderByReturnedDayDesc(User user, Book book, String status);
    Optional<Record> findFirstByUserAndBook_Title(User user, String title);
    boolean existsByUserAndBook_TitleAndStatus(User user, String title, String status);
    Optional<Record> findByBookAndStatus(Book book, String status);
    List<Record> findByStatusAndDueDayBefore(String status, LocalDate currentDate);
    List<Record> findByUser_UserIdAndStatus(int userId, String status);
    @Query("select r.book.bookId as bookId, count(r) as borrowCount " +
            "from Record r " +
            "where r.borrowDay >= :startDate " +
            "group by r.book.bookId " +
            "order by borrowCount desc")
    List<Object[]> findTrendingBooks(LocalDate startDate);
}
