package com.example.librarymanagement.dtos.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookModel {

    int bookId;
    String title;
    String author;
    String categoryName;
    String publisherName;
    String isbn;
    String imageUrl;
    int totalCopies;
    String status;
}
