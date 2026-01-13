package com.example.librarymanagement.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "publisher")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Publisher {

    @Id
    @Column(name = "publisher_id")
    String publisherId;

    @Column(name = "publisher_name")
    String publisherName;

    @Column(name = "address")
    String address;

    @Column(name = "contact")
    String contact;
}
