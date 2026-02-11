package com.example.librarymanagement.dtos.requests.book;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookAddRequest {

    @NotBlank(message = "Title must not be blank")
    String title;

    @NotBlank(message = "Author must not be blank")
    String author;

    @NotBlank(message = "Category name must not be blank")
    String categoryName;

    @NotBlank(message = "Publisher name must not be blank")
    String publisherName;
}
