package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.models.BorrowOrderModel;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.BorrowOrder;
import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.enums.ErrorCode;
import com.example.librarymanagement.enums.RecordStatus;
import com.example.librarymanagement.exception.AppException;
import com.example.librarymanagement.repositories.BorrowOrderRepository;
import com.example.librarymanagement.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PendingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BorrowOrderRepository borrowOrderRepository;

    public BorrowOrderModel toModel(BorrowOrder borrowOrder) {
        Book book = borrowOrder.getBook();
        return new BorrowOrderModel(
                borrowOrder.getBorrowRecordId(),
                borrowOrder.getUser().getFullName(),
                book!=null ? book.getBookId():null,
                borrowOrder.getTitle()!=null ? borrowOrder.getTitle(): borrowOrder.getBook().getTitle(),
                book!=null ? book.getAuthor():null,
                book!=null ? book.getImageUrl():null,
                borrowOrder.getBorrowDay(),
                borrowOrder.getBorrowDays(),
                borrowOrder.getDueDay(),
                borrowOrder.getReturnedDay(),
                borrowOrder.getStatus(),
                borrowOrder.getExtendCount()
        );
    }

    public List<BorrowOrderModel> getRecordPendingBorrow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

            List<BorrowOrder> borrowOrders = borrowOrderRepository.findByUser_UserIdAndStatus(userCurrent.getUserId(), RecordStatus.PENDING_APPROVE.name());
            return borrowOrders.stream()
                    .map(this::toModel)
                    .collect(Collectors.toList());
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    public List<BorrowOrderModel> getRecordPendingReturn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

            List<BorrowOrder> borrowOrders = borrowOrderRepository.findByUser_UserIdAndStatus(userCurrent.getUserId(), RecordStatus.PENDING_RETURN.name());
            return borrowOrders.stream()
                    .map(this::toModel)
                    .collect(Collectors.toList());
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    public String cancelPendingBorrow(int recordId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
            BorrowOrder borrowOrder = borrowOrderRepository.findById(recordId)
                    .orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND));
            if(!RecordStatus.PENDING_APPROVE.name().equals(borrowOrder.getStatus()))
                throw new IllegalStateException("The order cannot be canceled in its current state");
            if(!borrowOrder.getUser().equals(userCurrent))
                throw new AppException(ErrorCode.UNAUTHORIZED);

            borrowOrder.setStatus(RecordStatus.CANCELLED.name());
            borrowOrderRepository.save(borrowOrder);

            return "Record application cancelled successfully";
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }
}
