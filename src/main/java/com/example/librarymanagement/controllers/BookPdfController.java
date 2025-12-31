package com.example.librarymanagement.controllers;

import com.example.librarymanagement.services.BookPdfService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/pdf")
public class BookPdfController {

    @Autowired
    private BookPdfService bookPdfService;

    @GetMapping("/{bookId}/preview")
    public void previewBook(@PathVariable int bookId, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "inline; filename=\"preview.pdf\""
        );
        bookPdfService.streamPreviewPdf(bookId, response.getOutputStream());
    }
}
