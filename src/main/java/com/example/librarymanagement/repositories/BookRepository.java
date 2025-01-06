package com.example.librarymanagement.repositories;

import com.example.librarymanagement.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findAllByTitle(String title);
    Optional<Book> findByTitle(String title);
    Optional<Book> findByIsbn(String title);
    List<Book> findByAuthor(String author);
    List<Book> findByPublisher_PublisherName(String publisherName);
    List<Book> findByCategory_CategoryName(String categoryName);
}
