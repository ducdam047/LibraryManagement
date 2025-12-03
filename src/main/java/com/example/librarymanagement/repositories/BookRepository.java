package com.example.librarymanagement.repositories;

import com.example.librarymanagement.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findAllByTitle(String title);
    Optional<Book> findFirstByTitle(String title);
    Boolean existsByTitle(String title);
    Optional<Book> findFirstByTitleAndStatus(String title, String status);
    Optional<Book> findByIsbn(String title);
    List<Book> findByAuthor(String author);
    List<Book> findByPublisher_PublisherName(String publisherName);
    List<Book> findByCategory_CategoryName(String categoryName);
    List<Book> findByStatus(String status);
    long countByStatus(String status);
}
