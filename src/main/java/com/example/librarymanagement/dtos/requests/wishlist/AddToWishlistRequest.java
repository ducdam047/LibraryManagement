package com.example.librarymanagement.dtos.requests.wishlist;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddToWishlistRequest {

    String bookName;
}
