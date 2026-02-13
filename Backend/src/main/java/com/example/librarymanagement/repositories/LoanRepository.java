package com.example.librarymanagement.repositories;

import com.example.librarymanagement.dtos.models.BookTrending;
import com.example.librarymanagement.dtos.responses.chart.CategoryBorrowStat;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.Loan;
import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Integer> {

    Optional<Loan> findByBook(Book book);
    List<Loan> findByUser_UserIdOrderByBorrowDayDesc(int userId);
    List<Loan> findByUser_UserId(int userId);
    List<Loan> findByBorrowStatus(String status);
    int countByUserAndBorrowStatus(User user, LoanStatus status);
    Optional<Loan> findByUserAndBookAndBorrowStatus(User user, Book book, LoanStatus status);
    Optional<Loan> findByUserAndBookAndBorrowStatusIn(User user, Book book, List<LoanStatus> statusList);
    Optional<Loan> findFirstByUserAndBookAndBorrowStatusOrderByReturnedDayDesc(User user, Book book, String status);
    Optional<Loan> findFirstByUserAndBook(User user, Book book);
    boolean existsByUserAndTitleAndBorrowStatus(User user, String title, LoanStatus status);
    boolean existsByUserAndBorrowStatus(User user, LoanStatus status);
    Optional<Loan> findByBookAndBorrowStatus(Book book, String status);
    List<Loan> findByBorrowStatusAndDueDayBefore(LoanStatus status, LocalDate currentDate);
    List<Loan> findByUser_UserIdAndBorrowStatus(int userId, LoanStatus status);
    List<Loan> findByUser_UserIdAndBorrowStatusOrderByReturnedDayAsc(int userId, LoanStatus status);
    List<Loan> findByUser_UserIdAndBorrowStatusIn(int userId, List<LoanStatus> status);
    @Query("select r from Loan r where r.borrowStatus = 'PENDING_APPROVE'")
    List<Loan> getPendingApproveLoans();
    @Query("select r from Loan r where r.borrowStatus = 'PAID'")
    List<Loan> getPendingPaidLoans();
    @Query("select r from Loan r where r.borrowStatus = 'PENDING_RETURN'")
    List<Loan> getPendingReturnLoans();
    @Query("select r from Loan r where r.borrowStatus = 'OVERDUE'")
    List<Loan> getOverdueLoans();
    @Query("""
            select new com.example.librarymanagement.dtos.models.BookTrending(
                b,
                count(r)
            )
            from Loan r
            join r.book b
            where r.borrowDay >= :startDate
            group by b
            order by
                count(r) desc,
                max(r.borrowDay) desc
            """)
    List<BookTrending> findTrendingBooks(@Param("startDate") LocalDate startDate);
    @Query("select count(distinct r.user.id) from Loan r where r.borrowStatus = :borrowStatus")
    long countDistinctUserByBorrowStatus(@Param("borrowStatus") String status);
    long countByBorrowStatus(String status);
    @Query("select function('date', b.borrowDay), count(b) from Loan b " +
            "where b.borrowDay >= :startDate group by function('date', b.borrowDay)")
    List<Object[]> countBorrowedByDay(@Param("startDate") LocalDate startDate);
    @Query("select function('date', b.returnedDay), count(b) from Loan b " +
            "where b.returnedDay >= :startDate and b.returnedDay is not null " +
            "group by function('date', b.returnedDay)")
    List<Object[]> countReturnedByDay(@Param("startDate") LocalDate startDate);
    @Query("""
            select new com.example.librarymanagement.dtos.responses.chart.CategoryBorrowStat(
                r.book.category.categoryName,
                count(r)
            )
            from Loan r
            where r.borrowStatus in ('ACTIVE', 'RETURNED', 'OVERDUE')
            group by r.book.category.categoryName
            """)
    List<CategoryBorrowStat> getBorrowStatsByCategory();
}
