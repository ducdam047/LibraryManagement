package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.models.LoanModel;
import com.example.librarymanagement.dtos.requests.action.BorrowBookRequest;
import com.example.librarymanagement.dtos.requests.action.ExtendBookRequest;
import com.example.librarymanagement.dtos.requests.action.ReturnBookRequest;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.Loan;
import com.example.librarymanagement.entities.Payment;
import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.enums.*;
import com.example.librarymanagement.exception.AppException;
import com.example.librarymanagement.repositories.BookRepository;
import com.example.librarymanagement.repositories.LoanRepository;
import com.example.librarymanagement.repositories.PaymentRepository;
import com.example.librarymanagement.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final PaymentRepository paymentRepository;
    private final VnPayService vnPayService;

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

    @PreAuthorize("hasRole('USER')")
    public List<LoanModel> getLoanHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

            List<Loan> loans = loanRepository.findByUser_UserIdOrderByBorrowDayDesc(userCurrent.getUserId());
            return loans.stream()
                    .map(this::toModel)
                    .collect(Collectors.toList());
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<LoanModel> getLoanList(int userId) {
        List<Loan> loans = loanRepository.findByUser_UserId(userId);
        return loans.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('USER')")
    public List<LoanModel> getActiveOverdueLoanList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

            List<Loan> loans = loanRepository.findByUser_UserIdAndBorrowStatusIn(userCurrent.getUserId(), List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE));
            return loans.stream()
                    .map(this::toModel)
                    .collect(Collectors.toList());
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @PreAuthorize("hasRole('USER')")
    public List<LoanModel> getReturnedLoanList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

            List<Loan> loans = loanRepository.findByUser_UserIdAndBorrowStatusOrderByReturnedDayAsc(userCurrent.getUserId(), LoanStatus.RETURNED);
            return loans.stream()
                    .map(this::toModel)
                    .collect(Collectors.toList());
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @PreAuthorize("hasRole('USER')")
    public LoanModel getBorrowedBook(int bookId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
            Book borrowedBook = bookRepository.findById(bookId)
                    .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
            Loan loan = loanRepository.findByUserAndBookAndBorrowStatusIn(userCurrent, borrowedBook, List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE))
                    .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));
            return toModel(loan);
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @PreAuthorize("hasRole('USER')")
    public LoanModel getReturnedBook(int loanId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
            Loan loan = loanRepository.findById(loanId)
                    .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));
            if (loan.getUser().getUserId() != userCurrent.getUserId())
                throw new AppException(ErrorCode.UNAUTHORIZED);
            return toModel(loan);
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public LoanModel borrowBook(BorrowBookRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            boolean bookRequest = loanRepository.existsByUserAndTitleAndBorrowStatus(userCurrent, request.getTitle(), LoanStatus.PENDING_APPROVE);
            boolean bookExists = loanRepository.existsByUserAndTitleAndBorrowStatus(userCurrent, request.getTitle(), LoanStatus.ACTIVE);

            if(UserStatus.BANNED.equals(userCurrent.getStatus()))
                throw new AppException(ErrorCode.ACCOUNT_BANNED);

            int activeCount = loanRepository.countByUserAndBorrowStatus(userCurrent, LoanStatus.ACTIVE);
            int pendingCount = loanRepository.countByUserAndBorrowStatus(userCurrent, LoanStatus.PENDING_APPROVE);

            if(bookRequest)
                throw new AppException(ErrorCode.BOOK_REQUESTED);
            if(bookExists)
                throw new AppException(ErrorCode.BOOK_BORROWED);
            if(activeCount+pendingCount>=5)
                throw new AppException(ErrorCode.BORROW_LIMIT_REACHED);
            if(request.getBorrowDays()>14)
                throw new AppException(ErrorCode.BORROW_DAYS_EXCEEDED);
            int borrowDays = request.getBorrowDays();

            Loan loan = Loan.builder()
                    .user(userCurrent)
                    .book(null)
                    .title(request.getTitle())
                    .borrowDay(null)
                    .borrowDays(borrowDays)
                    .dueDay(null)
                    .returnedDay(null)
                    .borrowStatus(LoanStatus.PENDING_APPROVE)
                    .extendCount(0)
                    .build();
            loanRepository.save(loan);

            return toModel(loan);
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public LoanModel approveBorrow(int loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));
        if(!LoanStatus.PENDING_APPROVE.equals(loan.getBorrowStatus()))
            throw new AppException(ErrorCode.LOAN_NOT_FOUND);

        loan.setDepositRequired(BigDecimal.valueOf(50000));
        loan.setBorrowFee(BigDecimal.valueOf(20000));
        loan.setDepositPaid(false);
        loan.setBorrowFeePaid(false);
        loan.setTotalPenalty(BigDecimal.ZERO);
        loan.setBorrowStatus(LoanStatus.PENDING_PAYMENT);

        BigDecimal totalAmount = loan.getDepositRequired().add(loan.getBorrowFee());

        Payment payment = Payment.builder()
                .loan(loan)
                .user(loan.getUser())
                .amount(totalAmount)
                .type(PaymentType.BORROW_FEE)
                .status(PaymentStatus.PENDING)
                .transactionRef("LOAN_" + loan.getLoanId())
                .build();
        paymentRepository.save(payment);

        return toModel(loan);
    }

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public LoanModel confirmBorrow(int loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));

        if(!LoanStatus.PENDING_PAYMENT.equals(loan.getBorrowStatus()))
            throw new AppException(ErrorCode.INVALID_LOAN_STATE);

        Payment payment = paymentRepository.findByLoanAndStatus(loan, PaymentStatus.PENDING)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());

        loan.setBorrowStatus(LoanStatus.PAID);
        loan.setDepositPaid(true);
        loan.setBorrowFeePaid(true);

        return toModel(loan);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public LoanModel handoverBook(int loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));
        if(!LoanStatus.PAID.equals(loan.getBorrowStatus()))
            throw new AppException(ErrorCode.PAYMENT_NOT_COMPLETED);

        String title = loan.getTitle();
        if(title==null && loan.getBook()!=null)
            title = loan.getBook().getTitle();

        Book book = bookRepository.findFirstByTitleAndStatus(title, BookStatus.AVAILABLE)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_OUT_OF_STOCK));

        LocalDate borrowDay = LocalDate.now();
        LocalDate dueDay = borrowDay.plusDays(loan.getBorrowDays());

        loan.setBook(book);
        loan.setBorrowDay(borrowDay);
        loan.setDueDay(dueDay);
        loan.setBorrowStatus(LoanStatus.ACTIVE);

        List<Book> sameTitleBooks = bookRepository.findAllByTitle(title);
        for (Book b : sameTitleBooks) {
            b.setAvailableCopies(b.getAvailableCopies() - 1);
            b.setBorrowedCopies(b.getBorrowedCopies() + 1);
        }
        book.setStatus(BookStatus.BORROWED);

        User user = loan.getUser();
        user.setBookBorrowing(user.getBookBorrowing() + 1);
        user.setStatus(UserStatus.BORROWING);

        return toModel(loan);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public LoanModel rejectBorrow(int loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));
        if(!LoanStatus.PENDING_APPROVE.equals(loan.getBorrowStatus()))
            throw new AppException(ErrorCode.LOAN_NOT_FOUND);

        loan.setBorrowStatus(LoanStatus.REJECTED);
        return toModel(loan);
    }

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public String returnBook(ReturnBookRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            Book bookReturn = bookRepository.findById(request.getBookId())
                    .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
            Loan loan = loanRepository.findByUserAndBookAndBorrowStatusIn(userCurrent, bookReturn, List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE))
                    .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));

            loan.setBorrowStatus(LoanStatus.PENDING_RETURN);

            return "Book with title: " + bookReturn.getTitle() + " has been returned";
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public String confirmReturn(int loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));
        if(!LoanStatus.PENDING_RETURN.equals(loan.getBorrowStatus()))
            throw new AppException(ErrorCode.LOAN_NOT_FOUND);

        Book book = loan.getBook();
        User user = loan.getUser();

        loan.setReturnedDay(LocalDate.now());
        loan.setBorrowStatus(LoanStatus.RETURNED);

        List<Book> bookSameTitles = bookRepository.findAllByTitle(book.getTitle());
        for(Book b : bookSameTitles) {
            b.setAvailableCopies(b.getAvailableCopies() + 1);
            b.setBorrowedCopies(b.getBorrowedCopies() - 1);
        }
        book.setStatus(BookStatus.AVAILABLE);

        user.setBookBorrowing(user.getBookBorrowing() - 1);
        if(user.getBookBorrowing()==0 && user.getBanUtil()==null)
            user.setStatus(UserStatus.ACTIVE);

        return "Confirmation of successful book receipt";
    }

    @PreAuthorize("hasRole('USER')")
    public String extendBook(ExtendBookRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            Book bookExtend = bookRepository.findById(request.getBookId())
                    .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
            Loan loan = loanRepository.findByUserAndBookAndBorrowStatus(userCurrent, bookExtend, LoanStatus.ACTIVE)
                    .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));

            if(loan.getExtendCount()==2)
                throw new AppException(ErrorCode.EXTEND_LIMIT_EXCEEDED);
            if(request.getExtendDays()<=0)
                throw new AppException(ErrorCode.INVALID_EXTEND_DAY);
            LocalDate extendDay = LocalDate.now();
            if (extendDay.isAfter(loan.getDueDay()))
                throw new AppException(ErrorCode.EXTEND_DEADLINE_EXPIRED);
            if (request.getExtendDays() > 3)
                throw new AppException(ErrorCode.EXTEND_DAY_EXCEEDED);
            int extendDays = request.getExtendDays();
            loan.setDueDay(loan.getDueDay().plusDays(extendDays));
            loan.setExtendCount(loan.getExtendCount() + 1);
            loanRepository.save(loan);
            return "Book with title: " + bookExtend.getTitle() + " has been extended";
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @Transactional()
    @PreAuthorize("hasRole('USER')")
    public String getPaymentUrl(int loanId, String clientIp) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));

        if(!LoanStatus.PENDING_PAYMENT.equals(loan.getBorrowStatus()))
            throw new AppException(ErrorCode.INVALID_LOAN_STATE);

        Payment payment = paymentRepository.findByLoanAndStatus(loan, PaymentStatus.PENDING)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        return vnPayService.createPaymentUrl(payment, clientIp);
    }
}
