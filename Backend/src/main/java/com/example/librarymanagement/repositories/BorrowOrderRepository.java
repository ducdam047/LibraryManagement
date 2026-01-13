package com.example.librarymanagement.repositories;

import com.example.librarymanagement.dtos.models.BookTrending;
import com.example.librarymanagement.dtos.responses.chart.CategoryBorrowStat;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.BorrowOrder;
import com.example.librarymanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BorrowOrderRepository extends JpaRepository<BorrowOrder, Integer> {

    Optional<BorrowOrder> findByBook(Book book);
    List<BorrowOrder> findByUser_UserIdOrderByBorrowDayDesc(int userId);
    List<BorrowOrder> findByUser_UserId(int userId);
    List<BorrowOrder> findByBorrowStatus(String status);
    int countByUserAndBorrowStatus(User user, String status);
    Optional<BorrowOrder> findByUserAndBookAndBorrowStatus(User user, Book book, String status);
    Optional<BorrowOrder> findByUserAndBookAndBorrowStatusIn(User user, Book book, List<String> statusList);
    Optional<BorrowOrder> findFirstByUserAndBookAndBorrowStatusOrderByReturnedDayDesc(User user, Book book, String status);
    Optional<BorrowOrder> findFirstByUserAndBook(User user, Book book);
    boolean existsByUserAndTitleAndBorrowStatus(User user, String title, String status);
    boolean existsByUserAndBorrowStatus(User user, String status);
    Optional<BorrowOrder> findByBookAndBorrowStatus(Book book, String status);
    List<BorrowOrder> findByBorrowStatusAndDueDayBefore(String status, LocalDate currentDate);
    List<BorrowOrder> findByUser_UserIdAndBorrowStatus(int userId, String status);
    List<BorrowOrder> findByUser_UserIdAndBorrowStatusOrderByReturnedDayAsc(int userId, String status);
    List<BorrowOrder> findByUser_UserIdAndBorrowStatusIn(int userId, List<String> status);
    @Query("select r from BorrowOrder r where r.borrowStatus = 'PENDING_APPROVE'")
    List<BorrowOrder> getPendingApproveRecords();
    @Query("select r from BorrowOrder r where r.borrowStatus = 'PENDING_RETURN'")
    List<BorrowOrder> getPendingReturnRecords();
    @Query("select r from BorrowOrder r where r.borrowStatus = 'OVERDUE'")
    List<BorrowOrder> getOverdueRecords();
    @Query("""
            select new com.example.librarymanagement.dtos.models.BookTrending(
                b,
                count(r)
            )
            from BorrowOrder r
            join r.book b
            where r.borrowDay >= :startDate
            group by b
            order by
                count(r) desc,
                max(r.borrowDay) desc
            """)
    List<BookTrending> findTrendingBooks(@Param("startDate") LocalDate startDate);
    @Query("select count(distinct r.user.id) from BorrowOrder r where r.borrowStatus = :borrowStatus")
    long countDistinctUserByBorrowStatus(@Param("borrowStatus") String status);
    long countByBorrowStatus(String status);
    @Query("select function('date', b.borrowDay), count(b) from BorrowOrder b " +
            "where b.borrowDay >= :startDate group by function('date', b.borrowDay)")
    List<Object[]> countBorrowedByDay(@Param("startDate") LocalDate startDate);
    @Query("select function('date', b.returnedDay), count(b) from BorrowOrder b " +
            "where b.returnedDay >= :startDate and b.returnedDay is not null " +
            "group by function('date', b.returnedDay)")
    List<Object[]> countReturnedByDay(@Param("startDate") LocalDate startDate);
    @Query("""
            select new com.example.librarymanagement.dtos.responses.chart.CategoryBorrowStat(
                r.book.category.categoryName,
                count(r)
            )
            from BorrowOrder r
            where r.borrowStatus in ('ACTIVE', 'RETURNED', 'OVERDUE')
            group by r.book.category.categoryName
            """)
    List<CategoryBorrowStat> getBorrowStatsByCategory();
}
