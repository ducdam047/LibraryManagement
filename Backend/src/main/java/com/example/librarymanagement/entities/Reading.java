package com.example.librarymanagement.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "reading")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Reading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reading_id")
    int readingId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user")
    User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book")
    Book book;

    @Column(name = "page")
    int page;

    @Column(name = "last_day")
    LocalDate lastDay;
}
