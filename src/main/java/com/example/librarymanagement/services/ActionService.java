package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.models.BookModel;
import com.example.librarymanagement.dtos.models.BorrowRecordModel;
import com.example.librarymanagement.dtos.models.EvaluateModel;
import com.example.librarymanagement.dtos.requests.action.BorrowBookRequest;
import com.example.librarymanagement.dtos.requests.action.EvaluateBookRequest;
import com.example.librarymanagement.dtos.requests.action.ExtendBookRequest;
import com.example.librarymanagement.dtos.requests.action.ReturnBookRequest;
import com.example.librarymanagement.entities.*;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ActionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowRepository borrowRepository;

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
                evaluate.getBook().getTitle(),
                evaluate.getRating(),
                evaluate.getComment(),
                evaluate.getEvaluateDay()
        );
    }

    private List<BookModel> findBooks(Function<BookRepository, List<Book>> finder) {
        List<Book> books = finder.apply(bookRepository);
        if(books.isEmpty())
            throw new AppException(ErrorCode.BOOK_NOT_FOUND);
        return books.stream().map(this::toModel).collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('USER')")
    public List<BookModel> filterCategory(String category) {
        return findBooks(repo -> repo.findByCategory_CategoryName(category));
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
        BorrowRecord borrowRecord = borrowRepository.findByBook(bookExtend)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND));
        LocalDate extendDay = LocalDate.now();
        if(extendDay.isAfter(borrowRecord.getDueDay()))
            throw new RuntimeException("The extension deadline has expired");
        if(request.getExtendDays()>3)
            throw new RuntimeException("The extending period must not exceed 3 days");
        int extendDays = request.getExtendDays();
        borrowRecord.setDueDay(borrowRecord.getDueDay().plusDays(extendDays));
        borrowRepository.save(borrowRecord);
        return "Book with title: " + bookExtend.getTitle() + " has been extended";
    }

    public List<BookModel> getBooks() {
        return findBooks(BookRepository::findAll);
    }

    public BookModel searchTitle(String title) {
        Book book = bookRepository.findFirstByTitle(title)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
        return toModel(book);
    }

    public List<BookModel> searchAuthor(String author) {
        return findBooks(repo -> repo.findByAuthor(author));
    }

    public List<BookModel> searchPublisher(String publisherName) {
        return findBooks(repo -> repo.findByPublisher_PublisherName(publisherName));
    }
}
