package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.models.ReadingModel;
import com.example.librarymanagement.dtos.requests.reading.ReadingAddRequest;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.Reading;
import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.enums.ErrorCode;
import com.example.librarymanagement.exception.AppException;
import com.example.librarymanagement.repositories.BookRepository;
import com.example.librarymanagement.repositories.ReadingRepository;
import com.example.librarymanagement.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReadingService {

    @Autowired
    private ReadingRepository readingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;

    private ReadingModel toModel(Reading reading) {
        return new ReadingModel(
                reading.getReadingId(),
                reading.getBook().getBookId(),
                reading.getBook().getTitle(),
                reading.getBook().getImageUrl(),
                reading.getBook().getPdfPath(),
                reading.getPage(),
                reading.getLastDay()
        );
    }

    public List<Integer> getReadingBookIds(int userId) {
        return readingRepository.findByUser_UserId(userId)
                .stream()
                .map(r -> r.getBook().getBookId())
                .toList();
    }

    @PreAuthorize("hasRole('USER')")
    public List<ReadingModel> getReadingList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

            List<Reading> readings = readingRepository.findByUser_UserId(userCurrent.getUserId());
            List<ReadingModel> readingModels = new ArrayList<>();
            for(Reading reading : readings)
                readingModels.add(toModel(reading));

            return readingModels;
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @PreAuthorize("hasRole('USER')")
    public ReadingModel getReadingBook(int readingId) {
        Reading reading = readingRepository.findById(readingId).orElse(null);
        if(reading==null) return null;
        return toModel(reading);
    }

    @PreAuthorize("hasRole('USER')")
    public ReadingModel getReadingBookId(int bookId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
            Reading reading = readingRepository.findByUser_UserIdAndBook_BookId(userCurrent.getUserId(), bookId)
                    .orElseThrow(() -> new AppException(ErrorCode.READING_NOT_FOUND));
            return toModel(reading);
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @PreAuthorize("hasRole('USER')")
    public Reading addToReading(int bookId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
            Optional<Reading> existed = readingRepository.findByUser_UserIdAndBook_BookId(userCurrent.getUserId(), bookId);

            if(existed.isPresent())
                return existed.get();

            Reading reading = Reading.builder()
                    .user(userCurrent)
                    .book(book)
                    .page(1)
                    .lastDay(LocalDate.now())
                    .build();
            return readingRepository.save(reading);
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @PreAuthorize("hasRole('USER')")
    public ReadingModel saveReading(ReadingAddRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
            Book book = bookRepository.findById(request.getBookId())
                    .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

            Optional<Reading> readingOptional =
                    readingRepository.findByUser_UserIdAndBook_BookId(userCurrent.getUserId(), book.getBookId());
            Reading reading;

            if(readingOptional.isPresent()) {
                reading = readingOptional.get();
                reading.setPage(request.getPage());
                reading.setLastDay(LocalDate.now());
            } else {
                reading = new Reading();
                reading.setUser(userCurrent);
                reading.setBook(book);
                reading.setPage(request.getPage());
                reading.setLastDay(LocalDate.now());
            }

            readingRepository.save(reading);
            return toModel(reading);
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }
}
