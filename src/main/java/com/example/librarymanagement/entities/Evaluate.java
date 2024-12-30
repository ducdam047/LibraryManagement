package com.example.librarymanagement.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "evaluate")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Evaluate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluate_id")
    int evaluateId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user")
    User user;

    @Column(name = "title")
    String title;

    @Column(name = "rating")
    String rating;

    @Column(name = "comment")
    String comment;

    @Column(name = "evaluate_day")
    LocalDate evaluateDay;
}
