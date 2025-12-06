package com.example.librarymanagement.services;

import com.example.librarymanagement.entities.Record;
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
        List<Record> overDueRecords = recordRepository.findByStatusAndDueDayBefore(RecordStatus.ACTIVE.name(), today);

        System.out.println("Found " + overDueRecords.size() + " overdue records");

        for(Record record : overDueRecords) {
            User user = record.getUser();
            System.out.println("Processing overdue record for user: " + user.getFullName());
            record.setStatus(RecordStatus.OVERDUE.name());
            recordRepository.save(record);

            applyPenalty(user);

            System.out.println("User " + user.getFullName() + " is banned until" + user.getBanUtil());
            System.out.println("-------------------------------------------------------------------");
        }
    }

    private void applyPenalty(User user) {
        int banDays = 3;
        LocalDate today = LocalDate.now();

        if(user.getBanUtil()==null || user.getBanUtil().isBefore(today)) {
            user.setBanUtil(today.plusDays(banDays));
        } else {
            user.setBanUtil(user.getBanUtil().plusDays(banDays));
        }

        userRepository.save(user);
        System.out.println("Penalty applied. User banned util " + user.getBanUtil());
    }
}
