package com.example.librarymanagement.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "book")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    int bookId;

    @Column(name = "title")
    String title;

    @Column(name = "author")
    String author;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category")
    Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "publisher")
    Publisher publisher;

    @Column(name = "isbn")
    String isbn;

    @Column(name = "image")
    String imageUrl;

    @Column(name = "total_copies")
    int totalCopies;

    @Column(name = "available_copies")
    int availableCopies;

    @Column(name = "borrowed_copies")
    int borrowedCopies;

    @Column(name = "status")
    String status;

    @OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<Wishlist> wishlists;

    @OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<Evaluate> evaluates;
}
