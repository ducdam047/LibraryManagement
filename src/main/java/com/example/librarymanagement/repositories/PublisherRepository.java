package com.example.librarymanagement.repositories;

import com.example.librarymanagement.entities.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PublisherRepository extends JpaRepository<Publisher, String> {

    Optional<Publisher> findByPublisherName(String publisherName);
}
