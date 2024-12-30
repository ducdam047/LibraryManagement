package com.example.librarymanagement.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category {

    @Id
    @Column(name = "category_id")
    String categoryId;

    @Column(name = "category_name")
    String categoryName;

    @Column(name = "description")
    String description;
}
