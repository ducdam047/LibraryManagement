package com.example.librarymanagement.entities;

import com.example.librarymanagement.enums.UserRole;
import com.example.librarymanagement.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    int userId;

    @Column(name = "username")
    String username;

    @Column(name = "password")
    String password;

    @Column(name = "full_name")
    String fullName;

    @Column(name = "email")
    String email;

    @Column(name = "phone_number")
    String phoneNumber;

    @Column(name = "cid")
    String cid;

    @Column(name = "address")
    String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    UserStatus status;

    @Column(name = "ban_util")
    LocalDate banUtil;

    @Column(name = "book_borrowing")
    Integer bookBorrowing;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    UserRole role;
}
