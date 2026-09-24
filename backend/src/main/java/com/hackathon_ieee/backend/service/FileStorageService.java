package com.hackathon_ieee.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path uploadDirectory;

    public FileStorageService(@Value("${storage.upload-dir:/app/uploads}") String uploadDir) {
        this.uploadDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException("Upload directory cannot be created", exception);
        }
    }

    public String store(MultipartFile photo) {
        if (photo == null || photo.isEmpty()) {
            throw new IllegalArgumentException("photo is required");
        }

        String extension = extension(photo.getOriginalFilename());
        String filename = "cit-report-" + UUID.randomUUID() + extension;
        Path target = uploadDirectory.resolve(filename).normalize();
        if (!target.getParent().equals(uploadDirectory)) {
            throw new IllegalArgumentException("Invalid photo filename");
        }
        try {
            Files.copy(photo.getInputStream(), target);
            return filename;
        } catch (IOException exception) {
            throw new IllegalStateException("Photo cannot be stored", exception);
        }
    }

    private String extension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return ".jpg";
        }
        String value = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase(Locale.ROOT);
        return switch (value) {
            case ".jpg", ".jpeg", ".png", ".webp" -> value;
            default -> ".jpg";
        };
    }
}
