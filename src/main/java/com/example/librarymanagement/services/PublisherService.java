package com.example.librarymanagement.services;

import com.example.librarymanagement.entities.Publisher;
import com.example.librarymanagement.repositories.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublisherService {

    @Autowired
    private PublisherRepository publisherRepository;

    public List<Publisher> getAllPublisher() {
        return publisherRepository.findAll();
    }
}
