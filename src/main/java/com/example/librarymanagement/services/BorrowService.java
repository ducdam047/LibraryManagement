package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.models.BookModel;
import com.example.librarymanagement.dtos.models.BorrowRecordModel;
import com.example.librarymanagement.dtos.models.EvaluateModel;
import com.example.librarymanagement.dtos.requests.action.BorrowBookRequest;
import com.example.librarymanagement.dtos.requests.action.EvaluateBookRequest;
import com.example.librarymanagement.dtos.requests.action.ExtendBookRequest;
import com.example.librarymanagement.dtos.requests.action.ReturnBookRequest;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.BorrowRecord;
import com.example.librarymanagement.entities.Evaluate;
import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.enums.BookStatus;
import com.example.librarymanagement.enums.ErrorCode;
import com.example.librarymanagement.enums.RecordStatus;
import com.example.librarymanagement.enums.UserStatus;
import com.example.librarymanagement.exception.AppException;
import com.example.librarymanagement.repositories.BookRepository;
import com.example.librarymanagement.repositories.BorrowRepository;
import com.example.librarymanagement.repositories.EvaluateRepository;
import com.example.librarymanagement.repositories.UserRepository;
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
public class BorrowService {

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private EvaluateRepository evaluateRepository;

    public BookModel toModel(Book book) {
        return new BookModel(
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                book.getCategory().getCategoryName(),
                book.getPublisher().getPublisherName(),
                book.getIsbn(),
                book.getImageUrl(),
                book.getPdfUrl(),
                book.getTotalCopies(),
                book.getAvailableCopies(),
                book.getBorrowedCopies(),
                book.getStatus()
        );
    }

    public BorrowRecordModel toModel(BorrowRecord borrowRecord) {
        return new BorrowRecordModel(
                borrowRecord.getUser().getFullName(),
                borrowRecord.getBook().getTitle(),
                borrowRecord.getBorrowDay(),
                borrowRecord.getDueDay()
        );
    }

    public EvaluateModel toModel(Evaluate evaluate) {
        return new EvaluateModel(
                evaluate.getUser().getFullName(),
                evaluate.getTitle(),
                evaluate.getRating(),
                evaluate.getComment(),
                evaluate.getEvaluateDay()
        );
    }

    @PreAuthorize("hasRole('USER')")
    public List<BookModel> getBorrowedBookList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

            List<BorrowRecord> records = borrowRepository.findByUser_UserIdAndStatus(userCurrent.getUserId(), RecordStatus.BORROWED.name());
            return records.stream()
                    .map(BorrowRecord::getBook)
                    .map(this::toModel)
                    .collect(Collectors.toList());
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @PreAuthorize("hasRole('USER')")
    public BorrowRecordModel borrowBook(BorrowBookRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            Book bookBorrow = bookRepository.findById(request.getBookId())
                    .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
            boolean bookExists = borrowRepository.existsByUserAndBook_TitleAndStatus(userCurrent, request.getTitle(), RecordStatus.BORROWED.name());
            if(bookExists)
                throw new AppException(ErrorCode.BOOK_BORROWED);
            if(userCurrent.getStatus().equals("LOCKED"))
                throw new AppException(ErrorCode.ACCOUNT_LOCKED);
            if(userCurrent.getBookBorrowing()==3)
                throw new AppException(ErrorCode.BORROW_LIMIT_REACHED);
            if(request.getBorrowDays()>5)
                throw new AppException(ErrorCode.BORROW_DAYS_EXCEEDED);
            int borrowDays = request.getBorrowDays();
            LocalDate borrowDay = LocalDate.now();
            LocalDate dueDay = borrowDay.plusDays(borrowDays);

            BorrowRecord borrowRecord = BorrowRecord.builder()
                    .user(userCurrent)
                    .book(bookBorrow)
                    .borrowDay(borrowDay)
                    .dueDay(dueDay)
                    .status(RecordStatus.BORROWED.name())
                    .extendCount(0)
                    .build();
            borrowRepository.save(borrowRecord);

            List<Book> bookSameTitle = bookRepository.findAllByTitle(bookBorrow.getTitle());
            for(Book book : bookSameTitle) {
                book.setAvailableCopies(book.getAvailableCopies() - 1);
                book.setBorrowedCopies(book.getBorrowedCopies() + 1);
            }
            bookBorrow.setStatus(BookStatus.BORROWED.name());
            bookRepository.save(bookBorrow);

            userCurrent.setBookBorrowing(userCurrent.getBookBorrowing() + 1);
            userCurrent.setStatus(UserStatus.BORROWING.name());
            userRepository.save(userCurrent);

            return toModel(borrowRecord);
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
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
            BorrowRecord borrowRecord = borrowRepository.findByBookAndStatus(bookReturn, RecordStatus.BORROWED.name())
                    .orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND));

            borrowRecord.setStatus(RecordStatus.RETURNED.name());
            borrowRepository.save(borrowRecord);

            List<Book> bookSameTitle = bookRepository.findAllByTitle(bookReturn.getTitle());
            for(Book book : bookSameTitle) {
                book.setAvailableCopies(book.getAvailableCopies() + 1);
                book.setBorrowedCopies(book.getBorrowedCopies() - 1);
            }
            bookReturn.setStatus(BookStatus.AVAILABLE.name());
            bookRepository.save(bookReturn);

            userCurrent.setBookBorrowing(userCurrent.getBookBorrowing() - 1);
            if(userCurrent.getBookBorrowing()==0)
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
            BorrowRecord borrowRecord = borrowRepository.findByUserAndBookAndStatus(userCurrent, bookExtend, RecordStatus.BORROWED.name())
                    .orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND));

            if(borrowRecord.getExtendCount()==2)
                throw new AppException(ErrorCode.EXTEND_LIMIT_EXCEEDED);
            if(request.getExtendDays()<=0)
                throw new AppException(ErrorCode.INVALID_EXTEND_DAY);
            LocalDate extendDay = LocalDate.now();
            if (extendDay.isAfter(borrowRecord.getDueDay()))
                throw new AppException(ErrorCode.EXTEND_DEADLINE_EXPIRED);
            if (request.getExtendDays() > 3)
                throw new AppException(ErrorCode.EXTEND_DAY_EXCEEDED);
            int extendDays = request.getExtendDays();
            borrowRecord.setDueDay(borrowRecord.getDueDay().plusDays(extendDays));
            borrowRecord.setExtendCount(borrowRecord.getExtendCount() + 1);
            borrowRepository.save(borrowRecord);
            return "Book with title: " + bookExtend.getTitle() + " has been extended";
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @PreAuthorize("hasRole('USER')")
    public EvaluateModel evaluateBook(EvaluateBookRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            boolean bookExists = bookRepository.existsByTitle(request.getTitle());
            if(!bookExists) throw new AppException(ErrorCode.BOOK_NOT_FOUND);

            boolean evaluated = evaluateRepository.existsByUserAndTitle(userCurrent, request.getTitle());
            if(evaluated) throw new AppException(ErrorCode.BOOK_EVALUATED);

            BorrowRecord record = borrowRepository.findByUserAndBook_Title(userCurrent, request.getTitle())
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_BORROWED));
            if(!(record.getStatus().equals("BORROWED") || record.getStatus().equals("RETURNED")))
                throw new AppException(ErrorCode.NOT_ELIGIBLE_TO_EVALUATE);

            Evaluate evaluate = Evaluate.builder()
                    .user(userCurrent)
                    .title(request.getTitle())
                    .rating(request.getRating())
                    .comment(request.getComment())
                    .evaluateDay(LocalDate.now())
                    .build();
            evaluateRepository.save(evaluate);
            return toModel(evaluate);
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }
}
