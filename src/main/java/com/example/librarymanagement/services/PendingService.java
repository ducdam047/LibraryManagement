package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.models.RecordModel;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.Record;
import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.enums.ErrorCode;
import com.example.librarymanagement.enums.RecordStatus;
import com.example.librarymanagement.exception.AppException;
import com.example.librarymanagement.repositories.RecordRepository;
import com.example.librarymanagement.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PendingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecordRepository recordRepository;

    public RecordModel toModel(Record record) {
        Book book = record.getBook();
        return new RecordModel(
                record.getBorrowRecordId(),
                record.getUser().getFullName(),
                book!=null ? book.getBookId():null,
                record.getTitle()!=null ? record.getTitle():record.getBook().getTitle(),
                book!=null ? book.getAuthor():null,
                book!=null ? book.getImageUrl():null,
                record.getBorrowDay(),
                record.getBorrowDays(),
                record.getDueDay(),
                record.getReturnedDay(),
                record.getStatus(),
                record.getExtendCount()
        );
    }

    public List<RecordModel> getRecordPendingBorrow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

            List<Record> records = recordRepository.findByUser_UserIdAndStatus(userCurrent.getUserId(), RecordStatus.PENDING_APPROVE.name());
            return records.stream()
                    .map(this::toModel)
                    .collect(Collectors.toList());
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    public List<RecordModel> getRecordPendingReturn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

            List<Record> records = recordRepository.findByUser_UserIdAndStatus(userCurrent.getUserId(), RecordStatus.PENDING_RETURN.name());
            return records.stream()
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
            Record record = recordRepository.findById(recordId)
                    .orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND));
            if(!RecordStatus.PENDING_APPROVE.name().equals(record.getStatus()))
                throw new IllegalStateException("The order cannot be canceled in its current state");
            if(!record.getUser().equals(userCurrent))
                throw new AppException(ErrorCode.UNAUTHORIZED);

            record.setStatus(RecordStatus.CANCELLED.name());
            recordRepository.save(record);

            return "Record application cancelled successfully";
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }
}
