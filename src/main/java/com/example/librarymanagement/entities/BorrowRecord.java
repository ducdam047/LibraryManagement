package com.example.librarymanagement.entities;

import com.example.librarymanagement.enums.RecordStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "borrow_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "borrow_record_id")
    int borrowRecordId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user")
    User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book")
    Book book;

    @Column(name = "borrow_day")
    LocalDate borrowDay;

    @Column(name = "due_day")
    LocalDate dueDay;

    @Column(name = "status")
    String status;
}
