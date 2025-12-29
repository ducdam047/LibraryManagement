package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.models.RecordModel;
import com.example.librarymanagement.dtos.requests.action.BorrowBookRequest;
import com.example.librarymanagement.dtos.requests.action.ExtendBookRequest;
import com.example.librarymanagement.dtos.requests.action.ReturnBookRequest;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.Record;
import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.enums.BookStatus;
import com.example.librarymanagement.enums.ErrorCode;
import com.example.librarymanagement.enums.RecordStatus;
import com.example.librarymanagement.enums.UserStatus;
import com.example.librarymanagement.exception.AppException;
import com.example.librarymanagement.repositories.BookRepository;
import com.example.librarymanagement.repositories.RecordRepository;
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
public class RecordService {

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private EvaluateRepository evaluateRepository;

    public RecordModel toModel(Record record) {
        Book book = record.getBook();
        return new RecordModel(
                record.getBorrowRecordId(),
                record.getUser().getFullName(),
                book!=null ? book.getBookId():null,
                record.getTitle(),
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

    @PreAuthorize("hasRole('ADMIN')")
    public List<RecordModel> getRecordList(int userId) {
        List<Record> records = recordRepository.findByUser_UserId(userId);
        return records.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('USER')")
    public List<RecordModel> getActiveOverdueRecordList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

            List<Record> records = recordRepository.findByUser_UserIdAndStatusIn(userCurrent.getUserId(), List.of(RecordStatus.ACTIVE.name(), RecordStatus.OVERDUE.name()));
            return records.stream()
                    .map(this::toModel)
                    .collect(Collectors.toList());
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @PreAuthorize("hasRole('USER')")
    public List<RecordModel> getReturnedRecordList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

            List<Record> records = recordRepository.findByUser_UserIdAndStatus(userCurrent.getUserId(), RecordStatus.RETURNED.name());
            return records.stream()
                    .map(this::toModel)
                    .collect(Collectors.toList());
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @PreAuthorize("hasRole('USER')")
    public RecordModel getBorrowedBook(int bookId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
            Book borrowedBook = bookRepository.findById(bookId)
                    .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
            Record record = recordRepository.findByUserAndBookAndStatusIn(userCurrent, borrowedBook, List.of(RecordStatus.ACTIVE.name(), RecordStatus.OVERDUE.name()))
                    .orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND));
            return toModel(record);
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @PreAuthorize("hasRole('USER')")
    public RecordModel getReturnedBook(int recordId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
            Record record = recordRepository.findById(recordId)
                    .orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND));
            if (record.getUser().getUserId() != userCurrent.getUserId())
                throw new AppException(ErrorCode.UNAUTHORIZED);
            return toModel(record);
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @PreAuthorize("hasRole('USER')")
    public RecordModel borrowBook(BorrowBookRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            boolean bookExists = recordRepository.existsByUserAndTitleAndStatus(userCurrent, request.getTitle(), RecordStatus.ACTIVE.name());

            if(UserStatus.BANNED.name().equals(userCurrent.getStatus()))
                throw new AppException(ErrorCode.ACCOUNT_BANNED);

            int activeCount = recordRepository.countByUserAndStatus(userCurrent, RecordStatus.ACTIVE.name());
            int pendingCount = recordRepository.countByUserAndStatus(userCurrent, RecordStatus.PENDING.name());

            if(bookExists)
                throw new AppException(ErrorCode.BOOK_BORROWED);
            if(activeCount+pendingCount>=3)
                throw new AppException(ErrorCode.BORROW_LIMIT_REACHED);
            if(request.getBorrowDays()>7)
                throw new AppException(ErrorCode.BORROW_DAYS_EXCEEDED);
            int borrowDays = request.getBorrowDays();

            Record record = Record.builder()
                    .user(userCurrent)
                    .book(null)
                    .title(request.getTitle())
                    .borrowDay(null)
                    .borrowDays(borrowDays)
                    .dueDay(null)
                    .returnedDay(null)
                    .status(RecordStatus.PENDING.name())
                    .extendCount(0)
                    .build();
            recordRepository.save(record);

//            List<Book> bookSameTitle = bookRepository.findAllByTitle(bookBorrow.getTitle());
//            for(Book book : bookSameTitle) {
//                book.setAvailableCopies(book.getAvailableCopies() - 1);
//                book.setBorrowedCopies(book.getBorrowedCopies() + 1);
//            }
//            bookBorrow.setStatus(BookStatus.BORROWED.name());
//            bookRepository.save(bookBorrow);
//
//            userCurrent.setBookBorrowing(userCurrent.getBookBorrowing() + 1);
//            userCurrent.setStatus(UserStatus.BORROWING.name());
//            userRepository.save(userCurrent);

            return toModel(record);
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public RecordModel approveBorrow(int recordId) {
        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND));
        if(!RecordStatus.PENDING.name().equals(record.getStatus()))
            throw new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND);

        String title = record.getTitle();
        if(title==null && record.getBook()!=null)
            title = record.getBook().getTitle();

        Book book = bookRepository.findFirstByTitleAndStatus(title, BookStatus.AVAILABLE.name())
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_OUT_OF_STOCK));
        User user = record.getUser();

        LocalDate borrowDay = LocalDate.now();
        LocalDate dueDay = borrowDay.plusDays(record.getBorrowDays());

        record.setBook(book);
        record.setBorrowDay(borrowDay);
        record.setDueDay(dueDay);
        record.setStatus(RecordStatus.ACTIVE.name());

        List<Book> sameTitleBooks = bookRepository.findAllByTitle(title);
        for (Book b : sameTitleBooks) {
            b.setAvailableCopies(b.getAvailableCopies() - 1);
            b.setBorrowedCopies(b.getBorrowedCopies() + 1);
        }

        book.setStatus(BookStatus.BORROWED.name());
        user.setBookBorrowing(user.getBookBorrowing() + 1);
        user.setStatus(UserStatus.BORROWING.name());

        return toModel(record);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public RecordModel rejectBorrow(int recordId) {
        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND));
        if(!RecordStatus.PENDING.name().equals(record.getStatus()))
            throw new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND);

        record.setStatus(RecordStatus.REJECTED.name());
        return toModel(record);
    }

    @PreAuthorize("hasRole('USER')")
    public String returnBook(ReturnBookRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            Book bookReturn = bookRepository.findById(request.getBookId())
                    .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
            Record record = recordRepository.findByUserAndBookAndStatusIn(userCurrent, bookReturn, List.of(RecordStatus.ACTIVE.name(), RecordStatus.OVERDUE.name()))
                    .orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND));

            record.setReturnedDay(LocalDate.now());
            record.setStatus(RecordStatus.RETURNED.name());
            recordRepository.save(record);

            List<Book> bookSameTitle = bookRepository.findAllByTitle(bookReturn.getTitle());
            for(Book book : bookSameTitle) {
                book.setAvailableCopies(book.getAvailableCopies() + 1);
                book.setBorrowedCopies(book.getBorrowedCopies() - 1);
            }
            bookReturn.setStatus(BookStatus.AVAILABLE.name());
            bookRepository.save(bookReturn);

            userCurrent.setBookBorrowing(userCurrent.getBookBorrowing() - 1);
            if(userCurrent.getBookBorrowing()==0 && userCurrent.getBanUtil()==null)
                userCurrent.setStatus(UserStatus.ACTIVE.name());
            userRepository.save(userCurrent);

            return "Book with title: " + bookReturn.getTitle() + " has been returned";
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
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
            Record record = recordRepository.findByUserAndBookAndStatus(userCurrent, bookExtend, RecordStatus.ACTIVE.name())
                    .orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND));

            if(record.getExtendCount()==2)
                throw new AppException(ErrorCode.EXTEND_LIMIT_EXCEEDED);
            if(request.getExtendDays()<=0)
                throw new AppException(ErrorCode.INVALID_EXTEND_DAY);
            LocalDate extendDay = LocalDate.now();
            if (extendDay.isAfter(record.getDueDay()))
                throw new AppException(ErrorCode.EXTEND_DEADLINE_EXPIRED);
            if (request.getExtendDays() > 3)
                throw new AppException(ErrorCode.EXTEND_DAY_EXCEEDED);
            int extendDays = request.getExtendDays();
            record.setDueDay(record.getDueDay().plusDays(extendDays));
            record.setExtendCount(record.getExtendCount() + 1);
            recordRepository.save(record);
            return "Book with title: " + bookExtend.getTitle() + " has been extended";
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }
}
