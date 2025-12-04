package com.example.librarymanagement.services;

import com.example.librarymanagement.entities.BorrowRecord;
import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.enums.RecordStatus;
import com.example.librarymanagement.enums.UserStatus;
import com.example.librarymanagement.repositories.RecordRepository;
import com.example.librarymanagement.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PenaltyService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecordRepository recordRepository;

    @PostConstruct
    public void checkPenalty() {
        checkOverdueBorrowBook();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkOverdueBorrowBook() {
        LocalDate today = LocalDate.now();
        List<BorrowRecord> overDueRecords = recordRepository.findByStatusAndDueDayBefore(RecordStatus.BORROWED.name(), today);

        System.out.println("Found " + overDueRecords.size() + " overdue records");

        for(BorrowRecord record : overDueRecords) {
            User user = record.getUser();
            System.out.println("Processing overdue record for user: " + user.getFullName());
            record.setStatus(RecordStatus.OVERDUE.name());
            recordRepository.save(record);

            if(!user.getStatus().equals(UserStatus.LOCKED.name())) {
                user.setStatus(UserStatus.LOCKED.name());
                userRepository.save(user);

                applyPenalty(user, record);
                System.out.println("User " + user.getFullName() + " has been locked");
                System.out.println("-----------------------------------------------");
            } else {
                System.out.println("Processed");
            }
        }
    }

    private void applyPenalty(User user, BorrowRecord borrowRecord) {
        // Penalty
        System.out.println("Penalty!");
    }
}
