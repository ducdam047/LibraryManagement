package com.example.librarymanagement.dtos.requests.book;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookUpdateRequest {

    String title;
    String author;
    String categoryName;
    String publisherName;
}
