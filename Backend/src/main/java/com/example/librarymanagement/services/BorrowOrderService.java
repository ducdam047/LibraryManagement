package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.models.BorrowOrderModel;
import com.example.librarymanagement.dtos.requests.action.BorrowBookRequest;
import com.example.librarymanagement.dtos.requests.action.ExtendBookRequest;
import com.example.librarymanagement.dtos.requests.action.ReturnBookRequest;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.BorrowOrder;
import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.enums.BookStatus;
import com.example.librarymanagement.enums.ErrorCode;
import com.example.librarymanagement.enums.RecordStatus;
import com.example.librarymanagement.enums.UserStatus;
import com.example.librarymanagement.exception.AppException;
import com.example.librarymanagement.repositories.BookRepository;
import com.example.librarymanagement.repositories.BorrowOrderRepository;
import com.example.librarymanagement.repositories.EvaluateRepository;
import com.example.librarymanagement.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BorrowOrderService {

    @Autowired
    private BorrowOrderRepository borrowOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private EvaluateRepository evaluateRepository;

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

    @PreAuthorize("hasRole('USER')")
    public List<BorrowOrderModel> getRecordHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

            List<BorrowOrder> borrowOrders = borrowOrderRepository.findByUser_UserIdOrderByBorrowDayDesc(userCurrent.getUserId());
            return borrowOrders.stream()
                    .map(this::toModel)
                    .collect(Collectors.toList());
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<BorrowOrderModel> getRecordList(int userId) {
        List<BorrowOrder> borrowOrders = borrowOrderRepository.findByUser_UserId(userId);
        return borrowOrders.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('USER')")
    public List<BorrowOrderModel> getActiveOverdueRecordList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

            List<BorrowOrder> borrowOrders = borrowOrderRepository.findByUser_UserIdAndStatusIn(userCurrent.getUserId(), List.of(RecordStatus.ACTIVE.name(), RecordStatus.OVERDUE.name()));
            return borrowOrders.stream()
                    .map(this::toModel)
                    .collect(Collectors.toList());
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @PreAuthorize("hasRole('USER')")
    public List<BorrowOrderModel> getReturnedRecordList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

            List<BorrowOrder> borrowOrders = borrowOrderRepository.findByUser_UserIdAndStatusOrderByReturnedDayAsc(userCurrent.getUserId(), RecordStatus.RETURNED.name());
            return borrowOrders.stream()
                    .map(this::toModel)
                    .collect(Collectors.toList());
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @PreAuthorize("hasRole('USER')")
    public BorrowOrderModel getBorrowedBook(int bookId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
            Book borrowedBook = bookRepository.findById(bookId)
                    .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
            BorrowOrder borrowOrder = borrowOrderRepository.findByUserAndBookAndStatusIn(userCurrent, borrowedBook, List.of(RecordStatus.ACTIVE.name(), RecordStatus.OVERDUE.name()))
                    .orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND));
            return toModel(borrowOrder);
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @PreAuthorize("hasRole('USER')")
    public BorrowOrderModel getReturnedBook(int recordId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
            BorrowOrder borrowOrder = borrowOrderRepository.findById(recordId)
                    .orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND));
            if (borrowOrder.getUser().getUserId() != userCurrent.getUserId())
                throw new AppException(ErrorCode.UNAUTHORIZED);
            return toModel(borrowOrder);
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public BorrowOrderModel borrowBook(BorrowBookRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            boolean bookRequest = borrowOrderRepository.existsByUserAndTitleAndStatus(userCurrent, request.getTitle(), RecordStatus.PENDING_APPROVE.name());
            boolean bookExists = borrowOrderRepository.existsByUserAndTitleAndStatus(userCurrent, request.getTitle(), RecordStatus.ACTIVE.name());

            if(UserStatus.BANNED.name().equals(userCurrent.getStatus()))
                throw new AppException(ErrorCode.ACCOUNT_BANNED);

            int activeCount = borrowOrderRepository.countByUserAndStatus(userCurrent, RecordStatus.ACTIVE.name());
            int pendingCount = borrowOrderRepository.countByUserAndStatus(userCurrent, RecordStatus.PENDING_APPROVE.name());

            if(bookRequest)
                throw new AppException(ErrorCode.BOOK_REQUESTED);
            if(bookExists)
                throw new AppException(ErrorCode.BOOK_BORROWED);
            if(activeCount+pendingCount>=5)
                throw new AppException(ErrorCode.BORROW_LIMIT_REACHED);
            if(request.getBorrowDays()>14)
                throw new AppException(ErrorCode.BORROW_DAYS_EXCEEDED);
            int borrowDays = request.getBorrowDays();

            BorrowOrder borrowOrder = BorrowOrder.builder()
                    .user(userCurrent)
                    .book(null)
                    .title(request.getTitle())
                    .borrowDay(null)
                    .borrowDays(borrowDays)
                    .dueDay(null)
                    .returnedDay(null)
                    .status(RecordStatus.PENDING_APPROVE.name())
                    .extendCount(0)
                    .build();
            borrowOrderRepository.save(borrowOrder);

            return toModel(borrowOrder);
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public BorrowOrderModel approveBorrow(int recordId) {
        BorrowOrder borrowOrder = borrowOrderRepository.findById(recordId)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND));
        if(!RecordStatus.PENDING_APPROVE.name().equals(borrowOrder.getStatus()))
            throw new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND);

        String title = borrowOrder.getTitle();
        if(title==null && borrowOrder.getBook()!=null)
            title = borrowOrder.getBook().getTitle();

        User user = borrowOrder.getUser();
        Book book = bookRepository.findFirstByTitleAndStatus(title, BookStatus.AVAILABLE.name())
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_OUT_OF_STOCK));

        LocalDate borrowDay = LocalDate.now();
        LocalDate dueDay = borrowDay.plusDays(borrowOrder.getBorrowDays());

        borrowOrder.setBook(book);
        borrowOrder.setBorrowDay(borrowDay);
        borrowOrder.setDueDay(dueDay);
        borrowOrder.setStatus(RecordStatus.ACTIVE.name());

        List<Book> sameTitleBooks = bookRepository.findAllByTitle(title);
        for (Book b : sameTitleBooks) {
            b.setAvailableCopies(b.getAvailableCopies() - 1);
            b.setBorrowedCopies(b.getBorrowedCopies() + 1);
        }

        book.setStatus(BookStatus.BORROWED.name());
        user.setBookBorrowing(user.getBookBorrowing() + 1);
        user.setStatus(UserStatus.BORROWING.name());

        return toModel(borrowOrder);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public BorrowOrderModel rejectBorrow(int recordId) {
        BorrowOrder borrowOrder = borrowOrderRepository.findById(recordId)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND));
        if(!RecordStatus.PENDING_APPROVE.name().equals(borrowOrder.getStatus()))
            throw new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND);

        borrowOrder.setStatus(RecordStatus.REJECTED.name());
        return toModel(borrowOrder);
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
            BorrowOrder borrowOrder = borrowOrderRepository.findByUserAndBookAndStatusIn(userCurrent, bookReturn, List.of(RecordStatus.ACTIVE.name(), RecordStatus.OVERDUE.name()))
                    .orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND));

            borrowOrder.setStatus(RecordStatus.PENDING_RETURN.name());

            return "Book with title: " + bookReturn.getTitle() + " has been returned";
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public String confirmReturn(int recordId) {
        BorrowOrder borrowOrder = borrowOrderRepository.findById(recordId)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND));
        if(!RecordStatus.PENDING_RETURN.name().equals(borrowOrder.getStatus()))
            throw new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND);

        Book book = borrowOrder.getBook();
        User user = borrowOrder.getUser();

        borrowOrder.setReturnedDay(LocalDate.now());
        borrowOrder.setStatus(RecordStatus.RETURNED.name());

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
            BorrowOrder borrowOrder = borrowOrderRepository.findByUserAndBookAndStatus(userCurrent, bookExtend, RecordStatus.ACTIVE.name())
                    .orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND));

            if(borrowOrder.getExtendCount()==2)
                throw new AppException(ErrorCode.EXTEND_LIMIT_EXCEEDED);
            if(request.getExtendDays()<=0)
                throw new AppException(ErrorCode.INVALID_EXTEND_DAY);
            LocalDate extendDay = LocalDate.now();
            if (extendDay.isAfter(borrowOrder.getDueDay()))
                throw new AppException(ErrorCode.EXTEND_DEADLINE_EXPIRED);
            if (request.getExtendDays() > 3)
                throw new AppException(ErrorCode.EXTEND_DAY_EXCEEDED);
            int extendDays = request.getExtendDays();
            borrowOrder.setDueDay(borrowOrder.getDueDay().plusDays(extendDays));
            borrowOrder.setExtendCount(borrowOrder.getExtendCount() + 1);
            borrowOrderRepository.save(borrowOrder);
            return "Book with title: " + bookExtend.getTitle() + " has been extended";
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }
}
