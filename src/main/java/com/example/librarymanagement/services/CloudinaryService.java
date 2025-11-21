package com.example.librarymanagement.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) {
        validateFile(file, "image");
        try {
            Map uploadResult =cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "folder", "library/images"
                    )
            );
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Upload image failed: " + e.getMessage());
        }
    }

    public String uploadPdf(MultipartFile file) {
        validateFile(file, "pdf");
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "raw",
                            "folder", "library/pdf"
                    )
            );
            return uploadResult.get("secure_url").toString();
        } catch (Exception e) {
            throw new RuntimeException("Upload pdf failed: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file, String type) {
        if(file.isEmpty())
            throw new IllegalArgumentException("File cannot be empty");
        String contentType = file.getContentType();
        if(type.equals("image") && (contentType==null ||!contentType.startsWith("image/")))
            throw new IllegalArgumentException("File must be an image");
        if(type.equals("pdf") && !"application/pdf".equals(contentType))
            throw new IllegalArgumentException("File must be a PDF");
    }
}
