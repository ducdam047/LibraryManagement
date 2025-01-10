package com.example.librarymanagement.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "wishlist")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishlist_id")
    int wishlistId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book")
    Book book;

    @Column(name = "description")
    String description;

    @Column(name = "createdAt")
    LocalDate createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user")
    User user;
}
