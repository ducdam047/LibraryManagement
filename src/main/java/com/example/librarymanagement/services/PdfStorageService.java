package com.example.librarymanagement.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class PdfStorageService {

    @Value("${app.pdf.storage-path}")
    private String pdfStoragePath;

    public String savePdf(MultipartFile file) throws IOException {
        if(file==null || file.isEmpty()) return null;

        Files.createDirectories(Paths.get(pdfStoragePath));

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(pdfStoragePath, fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }
}
