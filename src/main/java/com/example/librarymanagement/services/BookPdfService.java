package com.example.librarymanagement.services;

import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.enums.ErrorCode;
import com.example.librarymanagement.exception.AppException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class BookPdfService {

    @Autowired
    private BookService bookService;

    public void streamPreviewPdf(int bookId, OutputStream outputStream) {
        Book book = bookService.getById(bookId);
        String pdfPath = book.getPdfPath();
        int previewPages = book.getPreviewPages();

        if(pdfPath==null)
            throw new AppException(ErrorCode.BOOK_HAS_NO_PDF);

        Path path = Paths.get(pdfPath);

        if(!Files.exists(path))
            throw new AppException(ErrorCode.PDF_FILE_NOT_FOUND);

        try (InputStream is = Files.newInputStream(path);
             PDDocument fullDoc = PDDocument.load(is);
             PDDocument previewDoc = new PDDocument()) {

            int totalPages = Math.min(previewPages, fullDoc.getNumberOfPages());
            for(int i=0; i<totalPages; i++)
                previewDoc.addPage(fullDoc.getPage(i));
            previewDoc.save(outputStream);
        } catch (IOException e) {
            throw new AppException(ErrorCode.PDF_PREVIEW_FAILED);
        }
    }
}
