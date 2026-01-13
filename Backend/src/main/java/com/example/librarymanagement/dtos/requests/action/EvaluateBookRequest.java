package com.example.librarymanagement.dtos.requests.action;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EvaluateBookRequest {

    int bookId;
    int rating;
    String comment;
}
