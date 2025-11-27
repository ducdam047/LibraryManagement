package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.models.BookModel;
import com.example.librarymanagement.dtos.requests.book.BookAddRequest;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.Category;
import com.example.librarymanagement.entities.Publisher;
import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.enums.BookStatus;
import com.example.librarymanagement.enums.ErrorCode;
import com.example.librarymanagement.exception.AppException;
import com.example.librarymanagement.repositories.BookRepository;
import com.example.librarymanagement.repositories.CategoryRepository;
import com.example.librarymanagement.repositories.PublisherRepository;
import com.example.librarymanagement.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ReadingService readingService;

    @Autowired
    private WishlistService wishlistService;

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

    public BookModel getBook(int bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
        return toModel(book);
    }

    public List<BookModel> getAvailableBooks() {
        List<Book> books = bookRepository.findByStatus(BookStatus.AVAILABLE.name());

        List<Book> uniqueBooks = books.stream()
                .collect(Collectors.toMap(
                        Book::getTitle,
                        b -> b,
                        (b1, b2) -> b1,
                        LinkedHashMap::new
                ))
                .values()
                .stream()
                .toList();
        return uniqueBooks.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if(authentication.getPrincipal() instanceof Jwt jwt) {
//            String email = jwt.getClaimAsString("sub");
//            User userCurrent = userRepository.findByEmail(email)
//                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
//            int userId = userCurrent.getUserId();
//            List<Integer> readingIds = readingService.getReadingBookIds(userId);
//            List<Integer> wishlistIds = wishlistService.getWishlistBookIds(userId);
//
//            return bookRepository.findByStatus(BookStatus.AVAILABLE.name())
//                    .stream()
//                    .filter(b -> !readingIds.contains(b.getBookId()))
//                    .filter(b -> !wishlistIds.contains(b.getBookId()))
//                    .map(this::toModel)
//                    .toList();
//        }
//        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public BookModel addBook(BookAddRequest request, MultipartFile imageFile, MultipartFile pdfFile) throws IOException {
        String imageUrl = cloudinaryService.uploadImage(imageFile);
        String pdfUrl = null;
        if(pdfFile!=null && !pdfFile.isEmpty())
            pdfUrl = cloudinaryService.uploadPdf(pdfFile);

        Category category = categoryRepository.findByCategoryName(request.getCategoryName())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        Publisher publisher = publisherRepository.findByPublisherName(request.getPublisherName())
                .orElseThrow(() -> new AppException(ErrorCode.PUBLISHER_NOT_FOUND));

        List<Book> books = bookRepository.findAllByTitle(request.getTitle());
        int updateTotalCopies = 1;
        int updateAvailableCopies = 1;
        if(!books.isEmpty()) {
            updateTotalCopies = books.get(0).getTotalCopies() + 1;
            updateAvailableCopies = books.get(0).getAvailableCopies() + 1;
            for(Book existingBook : books) {
                existingBook.setTotalCopies(updateTotalCopies);
                existingBook.setAvailableCopies(updateAvailableCopies);
            }
            bookRepository.saveAll(books);
        }

        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .category(category)
                .publisher(publisher)
                .isbn(request.getIsbn())
                .imageUrl(imageUrl)
                .pdfUrl(pdfUrl)
                .totalCopies(updateTotalCopies)
                .availableCopies(updateAvailableCopies)
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
