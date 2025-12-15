package com.example.librarymanagement.service;

import com.example.librarymanagement.dtos.models.BookModel;
import com.example.librarymanagement.dtos.requests.book.BookAddRequest;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.Category;
import com.example.librarymanagement.entities.Publisher;
import com.example.librarymanagement.repositories.BookRepository;
import com.example.librarymanagement.repositories.CategoryRepository;
import com.example.librarymanagement.repositories.PublisherRepository;
import com.example.librarymanagement.services.BookService;
import com.example.librarymanagement.services.CloudinaryService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private BookService bookService;

    @Test
    void addBook_success() throws IOException {
        // GIVEN
        BookAddRequest request = new BookAddRequest();
        request.setTitle("Tiếng gọi nơi hoang dã");
        request.setAuthor("Jack London");
        request.setCategoryName("Tiểu thuyết");
        request.setPublisherName("NXB Dân Trí");

        MultipartFile imageFile = mock(MultipartFile.class);
        MultipartFile pdfFile = mock(MultipartFile.class);

        when(cloudinaryService.uploadImage(imageFile))
                .thenReturn("image-url");
        when(cloudinaryService.uploadPdf(pdfFile))
                .thenReturn("pdf-url");

        Category category = new Category();
        category.setCategoryName("Tiểu thuyết");

        Publisher publisher = new Publisher();
        publisher.setPublisherName("NXB Dân Trí");

        when(categoryRepository.findByCategoryName("Tiểu thuyết"))
                .thenReturn(Optional.of(category));
        when(publisherRepository.findByPublisherName("NXB Dân Trí"))
                .thenReturn(Optional.of(publisher));
        when(bookRepository.findAllByTitle("Tiếng gọi nơi hoang dã"))
                .thenReturn(Collections.emptyList());

        // WHEN
        BookModel result = bookService.addBook(request, imageFile, pdfFile);

        //THEN
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Tiếng gọi nơi hoang dã");

        verify(bookRepository).save(any(Book.class));
    }
}
