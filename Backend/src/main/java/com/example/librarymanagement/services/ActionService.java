package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.models.BookModel;
import com.example.librarymanagement.entities.*;
import com.example.librarymanagement.enums.ErrorCode;
import com.example.librarymanagement.exception.AppException;
import com.example.librarymanagement.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActionService {

    private final BookRepository bookRepository;

    public BookModel toModel(Book book) {
        return new BookModel(
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                book.getCategory().getCategoryName(),
                book.getPublisher().getPublisherName(),
                book.getIsbn(),
                book.getImageUrl(),
                book.getPdfPath(),
                book.getTotalCopies(),
                book.getAvailableCopies(),
                book.getBorrowedCopies(),
                book.getStatus()
        );
    }

    private List<BookModel> findBooks(Function<BookRepository, List<Book>> finder) {
        List<Book> books = finder.apply(bookRepository);
        if(books.isEmpty())
            throw new AppException(ErrorCode.BOOK_NOT_FOUND);
        return books.stream().map(this::toModel).collect(Collectors.toList());
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
