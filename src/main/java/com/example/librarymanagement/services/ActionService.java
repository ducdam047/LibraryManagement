package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.models.BookModel;
import com.example.librarymanagement.dtos.models.RecordModel;
import com.example.librarymanagement.dtos.models.EvaluateModel;
import com.example.librarymanagement.entities.*;
import com.example.librarymanagement.entities.Record;
import com.example.librarymanagement.enums.ErrorCode;
import com.example.librarymanagement.exception.AppException;
import com.example.librarymanagement.repositories.BookRepository;
import com.example.librarymanagement.repositories.RecordRepository;
import com.example.librarymanagement.repositories.EvaluateRepository;
import com.example.librarymanagement.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

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
    private RecordRepository recordRepository;

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

    public EvaluateModel toModel(Evaluate evaluate) {
        return new EvaluateModel(
                evaluate.getUser().getFullName(),
                evaluate.getTitle(),
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
