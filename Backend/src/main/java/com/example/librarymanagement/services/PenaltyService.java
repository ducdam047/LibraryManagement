package com.example.librarymanagement.services;

import com.example.librarymanagement.entities.BorrowOrder;
import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.enums.RecordStatus;
import com.example.librarymanagement.enums.UserStatus;
import com.example.librarymanagement.repositories.BorrowOrderRepository;
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
    private BorrowOrderRepository borrowOrderRepository;

    @PostConstruct
    public void checkPenalty() {
        checkOverdueBorrowBook();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkOverdueBorrowBook() {
        LocalDate today = LocalDate.now();

        List<BorrowOrder> needOverdue = borrowOrderRepository.findByBorrowStatusAndDueDayBefore(RecordStatus.ACTIVE.name(), today);
        for(BorrowOrder borrowOrder : needOverdue) {
            borrowOrder.setBorrowStatus(RecordStatus.OVERDUE.name());
            borrowOrderRepository.save(borrowOrder);
        }

        List<User> allUsers = userRepository.findAll();
        for(User user : allUsers)
            applyPenalty(user);
    }

    private void applyPenalty(User user) {
        LocalDate today = LocalDate.now();
        int overdueCount = countOverdueOfUser(user);

        if(overdueCount>0) {
            user.setStatus(UserStatus.BANNED.name());
            LocalDate newBanUtil = today.plusDays(overdueCount*BAN_DAYS);
            user.setBanUtil(newBanUtil);
            userRepository.save(user);
            return;
        }

        if(user.getBanUtil()!=null && !today.isBefore(user.getBanUtil())) {
            user.setBanUtil(null);

            boolean hasActive = borrowOrderRepository.existsByUserAndBorrowStatus(user, RecordStatus.ACTIVE.name());
            if(hasActive) {
                user.setStatus(UserStatus.BORROWING.name());
            } else {
                user.setStatus(UserStatus.ACTIVE.name());
            }

            userRepository.save(user);
            return;
        }

        if(user.getBanUtil()!=null && today.isBefore(user.getBanUtil())) {
            user.setStatus(UserStatus.BANNED.name());
            userRepository.save(user);
            return;
        }
    }

    private int countOverdueOfUser(User user) {
        List<BorrowOrder> overdueBorrowOrders = borrowOrderRepository.findByUser_UserIdAndBorrowStatus(user.getUserId(), RecordStatus.OVERDUE.name());
        return overdueBorrowOrders.size();
    }
}
