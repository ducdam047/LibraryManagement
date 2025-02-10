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
import com.example.librarymanagement.repositories.BorrowRecordRepository;
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
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LibraryActionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

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
                evaluate.getBook().getTitle(),
                evaluate.getRating(),
                evaluate.getComment(),
                evaluate.getEvaluateDay()
        );
    }

    @PreAuthorize("hasRole('USER')")
    public BorrowRecordModel borrowBook(BorrowBookRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            Book bookBorrow = bookRepository.findFirstByTitleAndStatus(request.getTitle(), BookStatus.AVAILABLE.name())
                    .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
            boolean bookExists = borrowRecordRepository.existsByBook_Title(request.getTitle());
            if(bookExists)
                throw new RuntimeException("You are borrowing this book");
            if(userCurrent.getStatus().equals("LOCKED"))
                throw new RuntimeException("Your account has been locked");
            if(userCurrent.getBookBorrowing()==3)
                throw new RuntimeException("You have borrowed up to 3 books");
            if(request.getBorrowDays()>5)
                throw new RuntimeException("The borrowing period must not exceed 5 days");
            int borrowDays = request.getBorrowDays();
            LocalDate borrowDay = LocalDate.now();
            LocalDate dueDay = borrowDay.plusDays(borrowDays);

            BorrowRecord borrowRecord = BorrowRecord.builder()
                    .user(userCurrent)
                    .book(bookBorrow)
                    .borrowDay(borrowDay)
                    .dueDay(dueDay)
                    .status(RecordStatus.BORROWED.name())
                    .build();
            borrowRecordRepository.save(borrowRecord);

            userCurrent.setBookBorrowing(userCurrent.getBookBorrowing() + 1);
            userCurrent.setStatus(UserStatus.BORROWING.name());
            bookBorrow.setStatus(BookStatus.BORROWED.name());

            userRepository.save(userCurrent);
            bookRepository.save(bookBorrow);

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
            Book bookReturn = bookRepository.findByIsbn(request.getIsbn())
                    .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
            BorrowRecord borrowRecord = borrowRecordRepository.findByBookAndStatus(bookReturn, RecordStatus.BORROWED.name())
                    .orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND));
            if(userCurrent.getBookBorrowing()==1) {
                userCurrent.setStatus(UserStatus.ACTIVE.name());
            } else if (userCurrent.getBookBorrowing()==0) {
                throw new RuntimeException("You haven't borrowed any books yet");
            }
            userCurrent.setBookBorrowing(userCurrent.getBookBorrowing() - 1);
            bookReturn.setStatus(BookStatus.AVAILABLE.name());
            borrowRecord.setStatus(RecordStatus.RETURNED.name());

            userRepository.save(userCurrent);
            bookRepository.save(bookReturn);
            borrowRecordRepository.save(borrowRecord);

            return "Book with title: " + bookReturn.getTitle() + " has been returned";
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
            Book bookEvaluate = bookRepository.findFirstByTitle(request.getTitle())
                    .orElseThrow(() -> new AppException(ErrorCode.BOOK_EVALUATED));

            if(evaluateRepository.existsByBook_BookId(bookEvaluate.getBookId()))
                throw new AppException(ErrorCode.BOOK_EVALUATED);

            Evaluate evaluate = Evaluate.builder()
                    .user(userCurrent)
                    .book(bookEvaluate)
                    .rating(request.getRating())
                    .comment(request.getComment())
                    .evaluateDay(LocalDate.now())
                    .build();
            evaluateRepository.save(evaluate);
            return toModel(evaluate);
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @PreAuthorize("hasRole('USER')")
    public String extendBook(ExtendBookRequest request) {
        Book bookExtend = bookRepository.findByIsbn(request.getIsbn())
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
        BorrowRecord borrowRecord = borrowRecordRepository.findByBook(bookExtend)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND));
        LocalDate extendDay = LocalDate.now();
        if(extendDay.isAfter(borrowRecord.getDueDay()))
            throw new RuntimeException("The extension deadline has expired");
        if(request.getExtendDays()>3)
            throw new RuntimeException("The extending period must not exceed 3 days");
        int extendDays = request.getExtendDays();
        borrowRecord.setDueDay(borrowRecord.getDueDay().plusDays(extendDays));
        borrowRecordRepository.save(borrowRecord);
        return "Book with title: " + bookExtend.getTitle() + " has been extended";
    }

    private List<BookModel> findBooks(Function<BookRepository, List<Book>> finder) {
        List<Book> books = finder.apply(bookRepository);
        if(books.isEmpty())
            throw new AppException(ErrorCode.BOOK_NOT_FOUND);
        return books.stream().map(this::toModel).collect(Collectors.toList());
    }

    public List<BookModel> getBooks() {
        return findBooks(BookRepository::findAll);
    }

    public List<BookModel> searchAuthor(String author) {
        return findBooks(repo -> repo.findByAuthor(author));
    }

    public List<BookModel> searchPublisher(String publisherName) {
        return findBooks(repo -> repo.findByPublisher_PublisherName(publisherName));
    }

    public List<BookModel> searchCategory(String categoryName) {
        return findBooks(repo -> repo.findByCategory_CategoryName(categoryName));
    }
}
