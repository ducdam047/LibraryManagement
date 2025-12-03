package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.models.BookModel;
import com.example.librarymanagement.dtos.models.BorrowRecordModel;
import com.example.librarymanagement.dtos.requests.action.BorrowBookRequest;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.BorrowRecord;
import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.enums.BookStatus;
import com.example.librarymanagement.enums.ErrorCode;
import com.example.librarymanagement.enums.RecordStatus;
import com.example.librarymanagement.enums.UserStatus;
import com.example.librarymanagement.exception.AppException;
import com.example.librarymanagement.repositories.BookRepository;
import com.example.librarymanagement.repositories.BorrowRepository;
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
            boolean bookExists = borrowRepository.existsByUserAndBook_Title(userCurrent, request.getTitle());
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
}
