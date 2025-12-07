package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.models.DashboardModel;
import com.example.librarymanagement.enums.BookStatus;
import com.example.librarymanagement.enums.RecordStatus;
import com.example.librarymanagement.repositories.BookRepository;
import com.example.librarymanagement.repositories.RecordRepository;
import com.example.librarymanagement.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DashboardService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecordRepository recordRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public DashboardModel getSummary() {
        long totalBooks = bookRepository.count();
        long availableBooks = bookRepository.countByStatus(BookStatus.AVAILABLE.name());
        long borrowedBooks =bookRepository.countByStatus(BookStatus.BORROWED.name());

        long totalUser = userRepository.count();
        long borrowingUsers = recordRepository.countDistinctUserByStatus(RecordStatus.ACTIVE.name());
        long bannedUsers = userRepository.countByBanUtilAfter(LocalDate.now());

        long overdueRecords = recordRepository.countByStatusAndDueDayBefore(RecordStatus.ACTIVE.name(), LocalDate.now());

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
}
