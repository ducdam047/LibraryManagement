package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.requests.publisher.PublisherAddRequest;
import com.example.librarymanagement.entities.Publisher;
import com.example.librarymanagement.enums.ErrorCode;
import com.example.librarymanagement.exception.AppException;
import com.example.librarymanagement.repositories.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublisherService {

    @Autowired
    private PublisherRepository publisherRepository;

    public List<Publisher> getAllPublisher() {
        return publisherRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Publisher addPublisher(PublisherAddRequest request) {
        if(publisherRepository.existsByPublisherId(request.getPublisherId()))
            throw new AppException(ErrorCode.PUBLISHER_ID_EXISTS);
        if(publisherRepository.existsByPublisherNameIgnoreCase(request.getPublisherName()))
            throw new AppException(ErrorCode.PUBLISHER_NAME_EXISTS);

        Publisher publisher = new Publisher();
        publisher.setPublisherId(request.getPublisherId());
        publisher.setPublisherName(request.getPublisherName());

        return publisherRepository.save(publisher);
    }
}
