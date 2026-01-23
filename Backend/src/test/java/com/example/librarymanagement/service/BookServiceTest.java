package com.example.librarymanagement.service;

import com.example.librarymanagement.dtos.models.BookModel;
import com.example.librarymanagement.dtos.requests.book.BookAddRequest;
import com.example.librarymanagement.dtos.requests.book.BookUpdateRequest;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.Category;
import com.example.librarymanagement.entities.Publisher;
import com.example.librarymanagement.repositories.BookRepository;
import com.example.librarymanagement.repositories.CategoryRepository;
import com.example.librarymanagement.repositories.PublisherRepository;
import com.example.librarymanagement.services.BookService;
import com.example.librarymanagement.services.CloudinaryService;
import com.example.librarymanagement.services.PdfStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private PdfStorageService pdfStorageService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private PublisherRepository publisherRepository;
    @Mock
    private CloudinaryService cloudinaryService;
    @InjectMocks
    private BookService bookService;

    private BookAddRequest request;
    private BookUpdateRequest updateRequest;
    private MultipartFile imageFile;
    private MultipartFile pdfFile;
    private int bookId;
    private Book book;

    @BeforeEach
    void setUpAdd() {
        request = new BookAddRequest();
        request.setTitle("Tiếng Gọi Nơi Hoang Dã");
        request.setAuthor("Jack London");
        request.setCategoryName("Văn học - Tiểu thuyết");
        request.setPublisherName("NXB Tri Thức");

        imageFile = mock(MultipartFile.class);
        pdfFile = mock(MultipartFile.class);
    }

    @BeforeEach
    void setUpUpdate() {
        bookId = 1;
        book = new Book();
        book.setBookId(bookId);
        book.setTitle("Tên Cũ");
        book.setAuthor("Tác Giả Cũ");

        updateRequest = new BookUpdateRequest();
        updateRequest.setTitle("Tiếng Gọi Nơi Hoang Dã");
        updateRequest.setAuthor("Jack London");
        updateRequest.setCategoryName("Văn học - Tiểu thuyết");
        updateRequest.setPublisherName("NXB Tri Thức");

        imageFile = mock(MultipartFile.class);
        pdfFile = mock(MultipartFile.class);
    }

    @Test
    void addBook_success() throws IOException {
        // GIVEN
        when(cloudinaryService.uploadImage(imageFile))
                .thenReturn("image-url");
        when(pdfStorageService.savePdf(pdfFile))
                .thenReturn("pdf-url");

        when(bookRepository.findAllByTitle(anyString()))
                .thenReturn(Collections.emptyList());
        when(categoryRepository.findByCategoryName("Văn học - Tiểu thuyết"))
                .thenReturn(Optional.of(new Category()));
        when(publisherRepository.findByPublisherName("NXB Tri Thức"))
                .thenReturn(Optional.of(new Publisher()));

        // WHEN
        BookModel result = bookService.addBook(request, imageFile, pdfFile);

        //THEN
        assertThat(result).isNotNull();
//        assertThat(result).isNotNull();
//        assertThat(result.getTitle()).isEqualTo("Tiếng gọi nơi hoang dã");
//        assertThat(result.getAuthor()).isEqualTo("Jack London");
//        assertThat(result.getCategoryName()).isEqualTo("Tiểu thuyết");
//        assertThat(result.getPublisherName()).isEqualTo("NXB Dân Trí");
//        assertThat(result.getImageUrl()).isEqualTo("image-url");
//        assertThat(result.getPdfPath()).isEqualTo("pdf-url");
//
//        verify(bookRepository).save(any(Book.class));
//        verify(cloudinaryService).uploadImage(imageFile);
//        verify(categoryRepository).findByCategoryName("Tiểu thuyết");
//        verify(publisherRepository).findByPublisherName("NXB Dân Trí");
//        verify(pdfStorageService).savePdf(pdfFile);
    }

    @Test
    void addBook_throw_categoryNotFound() throws IOException {
        // GIVEN
        request.setCategoryName("Không tồn tại");

        when(categoryRepository.findByCategoryName("Không tồn tại"))
                .thenReturn(Optional.empty());

        // WHEN + THEN
        assertThatThrownBy(() -> bookService.addBook(request, imageFile, pdfFile))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void addBook_throw_publisherNotFound() throws IOException {
        // GIVEN
        request.setPublisherName("Không tồn tại");

        when(categoryRepository.findByCategoryName("Văn học - Tiểu thuyết"))
                .thenReturn(Optional.of(new Category()));
        when(publisherRepository.findByPublisherName("Không tồn tại"))
                .thenReturn(Optional.empty());

        // WHEN + THEN
        assertThatThrownBy(() -> bookService.addBook(request, imageFile, pdfFile))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void updateBook_success() throws IOException {
        // GIVEN
        Book book = new Book();
        when(bookRepository.findById(1))
                .thenReturn(Optional.of(book));
        when(categoryRepository.findByCategoryName("Văn học - Tiểu thuyết"))
                .thenReturn(Optional.of(new Category()));
        when(publisherRepository.findByPublisherName("NXB Tri Thức"))
                .thenReturn(Optional.of(new Publisher()));
        when(cloudinaryService.uploadImage(imageFile))
                .thenReturn("image-url");
        when(pdfStorageService.savePdf(pdfFile))
                .thenReturn("pdf-path");
        when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        Book result = bookService.updateBook(bookId, updateRequest, imageFile, pdfFile);

        // THEN
        assertThat(result.getTitle()).isEqualTo("Tiếng Gọi Nơi Hoang Dã");
        assertThat(result.getImageUrl()).isEqualTo("image-url");
    }
}
