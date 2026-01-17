package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.models.LoanModel;
import com.example.librarymanagement.dtos.requests.action.BorrowBookRequest;
import com.example.librarymanagement.dtos.requests.action.ExtendBookRequest;
import com.example.librarymanagement.dtos.requests.action.ReturnBookRequest;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.Loan;
import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.enums.BookStatus;
import com.example.librarymanagement.enums.ErrorCode;
import com.example.librarymanagement.enums.LoanStatus;
import com.example.librarymanagement.enums.UserStatus;
import com.example.librarymanagement.exception.AppException;
import com.example.librarymanagement.repositories.BookRepository;
import com.example.librarymanagement.repositories.LoanRepository;
import com.example.librarymanagement.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

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

            List<Loan> loans = loanRepository.findByUser_UserIdAndBorrowStatusIn(userCurrent.getUserId(), List.of(LoanStatus.ACTIVE.name(), LoanStatus.OVERDUE.name()));
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

            List<Loan> loans = loanRepository.findByUser_UserIdAndBorrowStatusOrderByReturnedDayAsc(userCurrent.getUserId(), LoanStatus.RETURNED.name());
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
            Loan loan = loanRepository.findByUserAndBookAndBorrowStatusIn(userCurrent, borrowedBook, List.of(LoanStatus.ACTIVE.name(), LoanStatus.OVERDUE.name()))
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
            boolean bookRequest = loanRepository.existsByUserAndTitleAndBorrowStatus(userCurrent, request.getTitle(), LoanStatus.PENDING_APPROVE.name());
            boolean bookExists = loanRepository.existsByUserAndTitleAndBorrowStatus(userCurrent, request.getTitle(), LoanStatus.ACTIVE.name());

            if(UserStatus.BANNED.name().equals(userCurrent.getStatus()))
                throw new AppException(ErrorCode.ACCOUNT_BANNED);

            int activeCount = loanRepository.countByUserAndBorrowStatus(userCurrent, LoanStatus.ACTIVE.name());
            int pendingCount = loanRepository.countByUserAndBorrowStatus(userCurrent, LoanStatus.PENDING_APPROVE.name());

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
                    .borrowStatus(LoanStatus.PENDING_APPROVE.name())
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
        if(!LoanStatus.PENDING_APPROVE.name().equals(loan.getBorrowStatus()))
            throw new AppException(ErrorCode.LOAN_NOT_FOUND);

        String title = loan.getTitle();
        if(title==null && loan.getBook()!=null)
            title = loan.getBook().getTitle();

        User user = loan.getUser();
        Book book = bookRepository.findFirstByTitleAndStatus(title, BookStatus.AVAILABLE.name())
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_OUT_OF_STOCK));

        LocalDate borrowDay = LocalDate.now();
        LocalDate dueDay = borrowDay.plusDays(loan.getBorrowDays());

        loan.setBook(book);
        loan.setBorrowDay(borrowDay);
        loan.setDueDay(dueDay);
        loan.setBorrowStatus(LoanStatus.ACTIVE.name());

        List<Book> sameTitleBooks = bookRepository.findAllByTitle(title);
        for (Book b : sameTitleBooks) {
            b.setAvailableCopies(b.getAvailableCopies() - 1);
            b.setBorrowedCopies(b.getBorrowedCopies() + 1);
        }

        book.setStatus(BookStatus.BORROWED.name());
        user.setBookBorrowing(user.getBookBorrowing() + 1);
        user.setStatus(UserStatus.BORROWING.name());

        return toModel(loan);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public LoanModel rejectBorrow(int loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));
        if(!LoanStatus.PENDING_APPROVE.name().equals(loan.getBorrowStatus()))
            throw new AppException(ErrorCode.LOAN_NOT_FOUND);

        loan.setBorrowStatus(LoanStatus.REJECTED.name());
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
            Loan loan = loanRepository.findByUserAndBookAndBorrowStatusIn(userCurrent, bookReturn, List.of(LoanStatus.ACTIVE.name(), LoanStatus.OVERDUE.name()))
                    .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));

            loan.setBorrowStatus(LoanStatus.PENDING_RETURN.name());

            return "Book with title: " + bookReturn.getTitle() + " has been returned";
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public String confirmReturn(int loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));
        if(!LoanStatus.PENDING_RETURN.name().equals(loan.getBorrowStatus()))
            throw new AppException(ErrorCode.LOAN_NOT_FOUND);

        Book book = loan.getBook();
        User user = loan.getUser();

        loan.setReturnedDay(LocalDate.now());
        loan.setBorrowStatus(LoanStatus.RETURNED.name());

        List<Book> bookSameTitles = bookRepository.findAllByTitle(book.getTitle());
        for(Book b : bookSameTitles) {
            b.setAvailableCopies(b.getAvailableCopies() + 1);
            b.setBorrowedCopies(b.getBorrowedCopies() - 1);
        }
        book.setStatus(BookStatus.AVAILABLE.name());

        user.setBookBorrowing(user.getBookBorrowing() - 1);
        if(user.getBookBorrowing()==0 && user.getBanUtil()==null)
            user.setStatus(UserStatus.ACTIVE.name());

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
            Loan loan = loanRepository.findByUserAndBookAndBorrowStatus(userCurrent, bookExtend, LoanStatus.ACTIVE.name())
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
}
