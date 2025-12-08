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

    private static final int BAN_DAYS = 3;

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
            record.setStatus(RecordStatus.OVERDUE.name());
            recordRepository.save(record);
            applyPenalty(user);
        }

        List<User> bannedUsers = userRepository.findByStatus(UserStatus.BANNED.name());
        for(User user : bannedUsers) {
            boolean hasOverdue = recordRepository.existsByUserAndStatus(user, RecordStatus.OVERDUE.name());
            updatePenalty(user, hasOverdue);
        }
    }

    private void applyPenalty(User user) {
        LocalDate today = LocalDate.now();

        if(!UserStatus.BANNED.name().equals(user.getStatus()))
            user.setStatus(UserStatus.BANNED.name());

        if(user.getBanUtil()==null || user.getBanUtil().isBefore(today)) {
            user.setBanUtil(today.plusDays(BAN_DAYS));
        } else {
            user.setBanUtil(user.getBanUtil().plusDays(BAN_DAYS));
        }

        userRepository.save(user);
        System.out.println("Applied overdue penalty to user " + user.getFullName() + " until " + user.getBanUtil());
    }

    private void updatePenalty(User user, boolean hasOverdue) {
        LocalDate today = LocalDate.now();

        if(hasOverdue) {
            if(!UserStatus.BANNED.name().equals(user.getStatus()))
                user.setStatus(UserStatus.BANNED.name());
            if(user.getBanUtil()==null || user.getBanUtil().isBefore(today))
                user.setBanUtil(today.plusDays(BAN_DAYS));
        } else {
            if(user.getBanUtil()!=null) {
                if(!user.getBanUtil().isAfter(today)) {
                    user.setBanUtil(null);
                    updateNormalStatus(user);
                } else {
                    if(!UserStatus.BANNED.name().equals(user.getStatus()))
                        user.setStatus(UserStatus.BANNED.name());
                }
            } else {
                updateNormalStatus(user);
            }
        }

        userRepository.save(user);
    }

    private void updateNormalStatus(User user) {
        boolean hasActiveRecord = recordRepository.existsByUserAndStatus(user, RecordStatus.ACTIVE.name());

        if(hasActiveRecord) {
            user.setStatus(UserStatus.BORROWING.name());
        } else {
            user.setStatus(UserStatus.ACTIVE.name());
        }
    }
}
