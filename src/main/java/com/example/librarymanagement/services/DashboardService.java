package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.models.*;
import com.example.librarymanagement.dtos.responses.chart.CategoryBorrowStat;
import com.example.librarymanagement.dtos.responses.chart.WeeklyStat;
import com.example.librarymanagement.dtos.responses.dashboard.DashboardResponse;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.Record;
import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.enums.BookStatus;
import com.example.librarymanagement.enums.RecordStatus;
import com.example.librarymanagement.enums.UserStatus;
import com.example.librarymanagement.repositories.BookRepository;
import com.example.librarymanagement.repositories.RecordRepository;
import com.example.librarymanagement.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecordRepository recordRepository;

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

    @PreAuthorize("hasRole('ADMIN')")
    public DashboardResponse getSummary() {
        long totalBooks = bookRepository.count();
        long availableBooks = bookRepository.countByStatus(BookStatus.AVAILABLE.name());
        long borrowedBooks =bookRepository.countByStatus(BookStatus.BORROWED.name());

        long totalUser = userRepository.count();
        long borrowingUsers = recordRepository.countDistinctUserByStatus(RecordStatus.ACTIVE.name());
        long bannedUsers = userRepository.countByStatus(UserStatus.BANNED.name());

        List<RecordModel> pendingRecords = recordRepository.getPendingRecords()
                .stream()
                .map(record -> {
                    Book book = record.getBook();
                    return new RecordModel(
                            record.getBorrowRecordId(),
                            record.getUser().getFullName(),
                            book != null ? book.getBookId():null,
                            record.getTitle(),
                            book != null ? book.getAuthor():null,
                            book != null ? book.getImageUrl():null,
                            record.getBorrowDay(),
                            record.getBorrowDays(),
                            record.getDueDay(),
                            record.getReturnedDay(),
                            record.getStatus(),
                            record.getExtendCount()
                    );
                })
                .collect(Collectors.toList());

        List<RecordModel> overdueRecords = recordRepository.getOverdueRecords()
                .stream()
                .map(record -> new RecordModel(
                        record.getBorrowRecordId(),
                        record.getUser().getFullName(),
                        record.getBook().getBookId(),
                        record.getBook().getTitle(),
                        record.getBook().getAuthor(),
                        record.getBook().getImageUrl(),
                        record.getBorrowDay(),
                        record.getBorrowDays(),
                        record.getDueDay(),
                        record.getReturnedDay(),
                        record.getStatus(),
                        record.getExtendCount()
                ))
                .collect(Collectors.toList());

        return DashboardResponse.builder()
                .totalBooks(totalBooks)
                .availableBooks(availableBooks)
                .borrowedBooks(borrowedBooks)
                .totalUsers(totalUser)
                .borrowingUsers(borrowingUsers)
                .bannedUsers(bannedUsers)
                .pendingRecords(pendingRecords)
                .overdueRecords(overdueRecords)
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
        for(Object[] row : recordRepository.countBorrowedByDay(start))
            borrowedMap.put(((java.sql.Date) row[0]).toLocalDate(), (Long) row[1]);

        Map<LocalDate, Long> returnedMap = new HashMap<>();
        for(Object[] row : recordRepository.countReturnedByDay(start))
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
        return recordRepository.getBorrowStatsByCategory();
    }
}
