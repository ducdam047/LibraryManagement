package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.models.BookModel;
import com.example.librarymanagement.dtos.requests.book.BookAddRequest;
import com.example.librarymanagement.dtos.requests.book.BookUpdateRequest;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.Category;
import com.example.librarymanagement.entities.Publisher;
import com.example.librarymanagement.enums.BookStatus;
import com.example.librarymanagement.enums.ErrorCode;
import com.example.librarymanagement.exception.AppException;
import com.example.librarymanagement.repositories.BookRepository;
import com.example.librarymanagement.repositories.CategoryRepository;
import com.example.librarymanagement.repositories.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    public BookModel toModel(Book book) {
        return new BookModel(
                book.getTitle(),
                book.getAuthor(),
                book.getCategory().getCategoryName(),
                book.getPublisher().getPublisherName(),
                book.getIsbn(),
                book.getTotalCopies(),
                book.getStatus()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public BookModel addBook(BookAddRequest request) {
        Category category = categoryRepository.findByCategoryName(request.getCategoryName())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        Publisher publisher = publisherRepository.findByPublisherName(request.getPublisherName())
                .orElseThrow(() -> new AppException(ErrorCode.PUBLISHER_NOT_FOUND));

        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .category(category)
                .publisher(publisher)
                .isbn(request.getIsbn())

                .status(BookStatus.AVAILABLE.name())
                .build();
        bookRepository.save(book);
        return toModel(book);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBook(int bookId) {
        if(!bookRepository.existsById(bookId))
            throw new AppException(ErrorCode.BOOK_NOT_FOUND);
        bookRepository.deleteById(bookId);
    }
}
