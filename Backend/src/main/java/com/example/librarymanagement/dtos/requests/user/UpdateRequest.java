package com.example.librarymanagement.dtos.requests.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateRequest {

    @Size(min = 6, max = 50, message = "Full name length is invalid")
    String fullName;

    @Size(min = 4, max = 20, message = "Username length must be between 4 and 20")
    String username;

    @Pattern(regexp = "^(0[0-9]{9})?$", message = "Phone number is invalid")
    String phoneNumber;

    @Size(min = 9, max = 12, message = "CID length is invalid")
    String cid;

    @Size(max = 255, message = "Address is too long")
    String address;
}
