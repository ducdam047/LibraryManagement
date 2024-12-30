package com.example.librarymanagement.dtos.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookModel {

    String title;
    String author;
    String categoryName;
    String publisherName;
    String isbn;
    String totalCopies;
    String status;
}
