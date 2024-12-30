package com.example.librarymanagement.dtos.requests.action;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EvaluateBookRequest {

    String title;
    String rating;
    String comment;
}
