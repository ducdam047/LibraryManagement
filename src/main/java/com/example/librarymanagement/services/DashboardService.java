package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.models.BookModel;
import com.example.librarymanagement.dtos.models.DashboardModel;
import com.example.librarymanagement.dtos.models.RecordModel;
import com.example.librarymanagement.dtos.models.UserModel;
import com.example.librarymanagement.entities.Book;
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
import java.util.List;
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
                book.getPdfUrl(),
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
    public DashboardModel getSummary() {
        long totalBooks = bookRepository.count();
        long availableBooks = bookRepository.countByStatus(BookStatus.AVAILABLE.name());
        long borrowedBooks =bookRepository.countByStatus(BookStatus.BORROWED.name());

        long totalUser = userRepository.count();
        long borrowingUsers = recordRepository.countDistinctUserByStatus(RecordStatus.ACTIVE.name());
        long bannedUsers = userRepository.countByStatus(UserStatus.BANNED.name());

        long overdueRecord = recordRepository.countByStatus(RecordStatus.OVERDUE.name());
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
                        record.getDueDay(),
                        record.getReturnedDay(),
                        record.getStatus(),
                        record.getExtendCount()
                ))
                .collect(Collectors.toList());

        return DashboardModel.builder()
                .totalBooks(totalBooks)
                .availableBooks(availableBooks)
                .borrowedBooks(borrowedBooks)
                .totalUsers(totalUser)
                .borrowingUsers(borrowingUsers)
                .bannedUsers(bannedUsers)
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
}
