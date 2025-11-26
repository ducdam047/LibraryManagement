package com.example.librarymanagement.dtos.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WishlistModel {

    int wishlistId;
    String bookName;
    String description;
    LocalDate createdAt;
    String fullName;
    String imageUrl;
    String pdfUrl;
    int bookId;
}
