package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.models.*;
import com.example.librarymanagement.dtos.responses.chart.CategoryBorrowStat;
import com.example.librarymanagement.dtos.responses.chart.WeeklyStat;
import com.example.librarymanagement.dtos.responses.dashboard.DashboardResponse;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.Loan;
import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.enums.BookStatus;
import com.example.librarymanagement.enums.LoanStatus;
import com.example.librarymanagement.enums.UserStatus;
import com.example.librarymanagement.repositories.BookRepository;
import com.example.librarymanagement.repositories.LoanRepository;
import com.example.librarymanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;

    public BookModel toModel(Book book) {
        return new BookModel(
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                book.getCategory().getCategoryName(),
                book.getPublisher().getPublisherName(),
                book.getIsbn(),
                book.getImageUrl(),
                book.getPdfPath(),
                book.getTotalCopies(),
                book.getAvailableCopies(),
                book.getBorrowedCopies(),
                book.getStatus()
        );
    }

    public UserModel toModel(User user) {
        return new UserModel(
                user.getFullName(),
                user.getStatus(),
                user.getBanUtil(),
                user.getBookBorrowing()
        );
    }

    public LoanModel toModel(Loan loan) {
        Book book = loan.getBook();
        return new LoanModel(
                loan.getLoanId(),
                loan.getUser().getFullName(),
                book != null ? book.getBookId():null,
                loan.getTitle(),
                book != null ? book.getAuthor():null,
                book != null ? book.getImageUrl():null,
                loan.getBorrowDay(),
                loan.getBorrowDays(),
                loan.getDueDay(),
                loan.getReturnedDay(),
                loan.getBorrowStatus(),
                loan.getDepositRequired(),
                loan.getDepositPaid(),
                loan.getBorrowFee(),
                loan.getBorrowFeePaid(),
                loan.getTotalPenalty(),
                loan.getExtendCount()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public DashboardResponse getSummary() {
        long totalBooks = bookRepository.count();
        long availableBooks = bookRepository.countByStatus(BookStatus.AVAILABLE.name());
        long borrowedBooks =bookRepository.countByStatus(BookStatus.BORROWED.name());

        long totalUser = userRepository.count();
        long borrowingUsers = loanRepository.countDistinctUserByBorrowStatus(LoanStatus.ACTIVE.name());
        long bannedUsers = userRepository.countByStatus(UserStatus.BANNED.name());

        List<LoanModel> pendingApproveLoans = loanRepository.getPendingApproveLoans()
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        List<LoanModel> pendingPaidLoans = loanRepository.getPendingPaidLoans()
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        List<LoanModel> pendingReturnLoans = loanRepository.getPendingReturnLoans()
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        List<LoanModel> overdueLoans = loanRepository.getOverdueLoans()
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return DashboardResponse.builder()
                .totalBooks(totalBooks)
                .availableBooks(availableBooks)
                .borrowedBooks(borrowedBooks)
                .totalUsers(totalUser)
                .borrowingUsers(borrowingUsers)
                .bannedUsers(bannedUsers)
                .pendingApproveLoans(pendingApproveLoans)
                .pendingPaidLoans(pendingPaidLoans)
                .pendingReturnLoans(pendingReturnLoans)
                .overdueLoans(overdueLoans)
                .build();
    }

    public List<BookModel> getDashboardBooks(String status) {
        List<Book> books;
        if(status==null || status.isEmpty()) {
            books = bookRepository.findAll();
        } else {
            books = bookRepository.findByStatus(status.toUpperCase());
        }
        return books.stream()
                .map(this::toModel)
                .toList();
    }

    public List<UserModel> getDashboardUsers(String status) {
        List<User> users;
        if(status==null || status.isEmpty()) {
            users = userRepository.findAll();
        } else {
            users = userRepository.findByStatus(status);
        }
        return users.stream()
                .map(this::toModel)
                .toList();
    }

    public List<WeeklyStat> getColumnChart() {
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(6);

        Map<LocalDate, Long> borrowedMap = new HashMap<>();
        for(Object[] row : loanRepository.countBorrowedByDay(start))
            borrowedMap.put(((java.sql.Date) row[0]).toLocalDate(), (Long) row[1]);

        Map<LocalDate, Long> returnedMap = new HashMap<>();
        for(Object[] row : loanRepository.countReturnedByDay(start))
            returnedMap.put(((java.sql.Date) row[0]).toLocalDate(), (Long) row[1]);

        List<WeeklyStat> result = new ArrayList<>();
        for(int i=0; i<7; i++) {
            LocalDate d = start.plusDays(i);
            result.add(
                    new WeeklyStat(d.getDayOfWeek().name().substring(0, 3),
                            borrowedMap.getOrDefault(d, 0L),
                            returnedMap.getOrDefault(d, 0L)
                    )
            );
        }

        return result;
    }

    public List<CategoryBorrowStat> getPieChart() {
        return loanRepository.getBorrowStatsByCategory();
    }
}
