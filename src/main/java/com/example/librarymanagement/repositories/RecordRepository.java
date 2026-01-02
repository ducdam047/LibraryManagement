package com.example.librarymanagement.repositories;

import com.example.librarymanagement.dtos.responses.chart.CategoryBorrowStat;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.Record;
import com.example.librarymanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record, Integer> {

    Optional<Record> findByBook(Book book);
    List<Record> findByUser_UserId(int userId);
    List<Record> findByStatus(String status);
    int countByUserAndStatus(User user, String status);
    Optional<Record> findByUserAndBookAndStatus(User user, Book book, String status);
    Optional<Record> findByUserAndBookAndStatusIn(User user, Book book, List<String> statusList);
    Optional<Record> findFirstByUserAndBookAndStatusOrderByReturnedDayDesc(User user, Book book, String status);
    Optional<Record> findFirstByUserAndBook(User user, Book book);
    boolean existsByUserAndTitleAndStatus(User user, String title, String status);
    boolean existsByUserAndStatus(User user, String status);
    Optional<Record> findByBookAndStatus(Book book, String status);
    List<Record> findByStatusAndDueDayBefore(String status, LocalDate currentDate);
    List<Record> findByUser_UserIdAndStatus(int userId, String status);
    List<Record> findByUser_UserIdAndStatusIn(int userId, List<String> status);
    @Query("select r from Record r where r.status = 'PENDING_APPROVE'")
    List<Record> getPendingApproveRecords();
    @Query("select r from Record r where r.status = 'PENDING_RETURN'")
    List<Record> getPendingReturnRecords();
    @Query("select r from Record r where r.status = 'OVERDUE'")
    List<Record> getOverdueRecords();
    @Query("select r.book.bookId as bookId, count(r) as borrowCount " +
            "from Record r " +
            "where r.borrowDay >= :startDate " +
            "group by r.book.bookId " +
            "order by borrowCount desc")
    List<Object[]> findTrendingBooks(LocalDate startDate);
    @Query("select count(distinct r.user.id) from Record r where r.status = :status")
    long countDistinctUserByStatus(@Param("status") String status);
    long countByStatus(String status);
    @Query("select function('date', b.borrowDay), count(b) from Record b " +
            "where b.borrowDay >= :startDate group by function('date', b.borrowDay)")
    List<Object[]> countBorrowedByDay(@Param("startDate") LocalDate startDate);
    @Query("select function('date', b.returnedDay), count(b) from Record b " +
            "where b.returnedDay >= :startDate and b.returnedDay is not null " +
            "group by function('date', b.returnedDay)")
    List<Object[]> countReturnedByDay(@Param("startDate") LocalDate startDate);
    @Query("""
            select new com.example.librarymanagement.dtos.responses.chart.CategoryBorrowStat(
                r.book.category.categoryName,
                count(r)
            )
            from Record r
            where r.status in ('ACTIVE', 'RETURNED', 'OVERDUE')
            group by r.book.category.categoryName
            """)
    List<CategoryBorrowStat> getBorrowStatsByCategory();
}
