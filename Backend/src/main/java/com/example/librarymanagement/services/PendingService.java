package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.models.LoanModel;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.Loan;
import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.enums.ErrorCode;
import com.example.librarymanagement.enums.LoanStatus;
import com.example.librarymanagement.exception.AppException;
import com.example.librarymanagement.repositories.LoanRepository;
import com.example.librarymanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PendingService {

    private final UserRepository userRepository;
    private final LoanRepository loanRepository;

    public LoanModel toModel(Loan loan) {
        Book book = loan.getBook();
        return new LoanModel(
                loan.getLoanId(),
                loan.getUser().getFullName(),
                book!=null ? book.getBookId():null,
                loan.getTitle()!=null ? loan.getTitle(): loan.getBook().getTitle(),
                book!=null ? book.getAuthor():null,
                book!=null ? book.getImageUrl():null,
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

    public List<LoanModel> getLoanPendingBorrow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

            List<Loan> loans = loanRepository.findByUser_UserIdAndBorrowStatusIn(userCurrent.getUserId(), List.of(LoanStatus.PENDING_APPROVE.name(), LoanStatus.PENDING_PAYMENT.name()));
            return loans.stream()
                    .map(this::toModel)
                    .collect(Collectors.toList());
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    public List<LoanModel> getLoanPendingReturn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

            List<Loan> loans = loanRepository.findByUser_UserIdAndBorrowStatus(userCurrent.getUserId(), LoanStatus.PENDING_RETURN.name());
            return loans.stream()
                    .map(this::toModel)
                    .collect(Collectors.toList());
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    public String cancelPendingBorrow(int loanId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
            Loan loan = loanRepository.findById(loanId)
                    .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));
            if(!LoanStatus.PENDING_APPROVE.name().equals(loan.getBorrowStatus()))
                throw new IllegalStateException("The loan cannot be canceled in its current state");
            if(!loan.getUser().equals(userCurrent))
                throw new AppException(ErrorCode.UNAUTHORIZED);

            loan.setBorrowStatus(LoanStatus.CANCELLED.name());
            loanRepository.save(loan);

            return "Loan application cancelled successfully";
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }
}
