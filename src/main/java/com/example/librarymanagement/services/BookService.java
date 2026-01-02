package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.models.BookModel;
import com.example.librarymanagement.dtos.requests.book.BookAddRequest;
import com.example.librarymanagement.dtos.requests.book.BookUpdateRequest;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.Category;
import com.example.librarymanagement.entities.Publisher;
import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.enums.BookStatus;
import com.example.librarymanagement.enums.ErrorCode;
import com.example.librarymanagement.exception.AppException;
import com.example.librarymanagement.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PdfStorageService pdfStorageService;

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

    @Autowired
    private RecordRepository recordRepository;

    public Book getById(int bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
    }

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

    public BookModel getBook(String title) {
        Book book = bookRepository.findFirstByTitle(title)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
        return toModel(book);
    }

    public List<BookModel> getFeaturedBooks() {
        List<Book> books = bookRepository.findAll();
        Collections.shuffle(books);

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
                .limit(15)
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public List<BookModel> getTrendingBooks(int limit) {
        LocalDate startDate = LocalDate.now().minusDays(7);
        List<Object[]> data = recordRepository.findTrendingBooks(startDate);
        return data.stream()
                .limit(limit)
                .map(row -> {
                    Integer bookId = (Integer) row[0];
                    Book book = bookRepository.findById(bookId).orElse(null);
                    return book != null ? toModel(book) : null;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public BookModel addBook(BookAddRequest request, MultipartFile imageFile, MultipartFile pdfFile) throws IOException {
        String imageUrl = cloudinaryService.uploadImage(imageFile);
        String pdfPath = null;
        if(pdfFile!=null && !pdfFile.isEmpty())
            pdfPath = pdfStorageService.savePdf(pdfFile);

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
                .imageUrl(imageUrl)
                .pdfPath(pdfPath)
                .previewPages(10)
                .totalCopies(updateTotalCopies)
                .availableCopies(updateAvailableCopies)
                .status(BookStatus.AVAILABLE.name())
                .build();

        bookRepository.save(book);
        return toModel(book);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Book updateBook(int bookId, BookUpdateRequest request, MultipartFile imageFile, MultipartFile pdfFile) throws IOException {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        if(imageFile!=null && !imageFile.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(imageFile);
            book.setImageUrl(imageUrl);
        }

        if(pdfFile!=null && !pdfFile.isEmpty()) {
            String pdfPath = pdfStorageService.savePdf(pdfFile);
            book.setPdfPath(pdfPath);
        }

        Category category = categoryRepository.findByCategoryName(request.getCategoryName())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        Publisher publisher = publisherRepository.findByPublisherName(request.getPublisherName())
                .orElseThrow(() -> new AppException(ErrorCode.PUBLISHER_NOT_FOUND));

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setCategory(category);
        book.setPublisher(publisher);

        return bookRepository.save(book);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBook(int bookId) {
        if(!bookRepository.existsById(bookId))
            throw new AppException(ErrorCode.BOOK_NOT_FOUND);
        bookRepository.deleteById(bookId);
    }
}
