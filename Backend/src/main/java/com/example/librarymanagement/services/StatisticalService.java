package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.models.BookModel;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.enums.BookStatus;
import com.example.librarymanagement.enums.UserStatus;
import com.example.librarymanagement.repositories.BookRepository;
import com.example.librarymanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticalService {

    private final BookRepository bookRepository;
    private final BookService bookService;
    private final UserRepository userRepository;

    public long countAllBook() {
        return bookRepository.count();
    }

    public long countBookAvailable() {
        return bookRepository.countByStatus(BookStatus.AVAILABLE.name());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public long countBookBorrowing() {
        return bookRepository.countByStatus(BookStatus.BORROWED.name());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<BookModel> bookBorrowing() {
        List<Book> books = bookRepository.findByStatus(BookStatus.BORROWED.name());
        List<BookModel> bookModels = new ArrayList<>();
        for(Book book : books) {
            BookModel bookModel = bookService.toModel(book);
            bookModels.add(bookModel);
        }
        return bookModels;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public long countUserLocked() {
        return userRepository.countByStatus(UserStatus.LOCKED.name());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<User> userLocked() {
        return userRepository.findByStatus(UserStatus.LOCKED.name());
    }
}
