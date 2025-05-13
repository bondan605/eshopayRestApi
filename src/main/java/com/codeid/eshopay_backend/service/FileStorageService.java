package com.codeid.eshopay_backend.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) throws Exception {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.fileStorageLocation);
    }

    public String storeFileWithRandomName(MultipartFile file) throws Exception {
        // Dapatkan ekstensi file asli
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";

        if (originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        // Generate nama file random
        String randomFileName = UUID.randomUUID().toString() + fileExtension;

        try {
            // Validasi nama file
            if (randomFileName.contains("..")) {
                throw new Exception("Nama file tidak valid: " + randomFileName);
            }

            // Simpan file
            Path targetLocation = this.fileStorageLocation.resolve(randomFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return randomFileName;
        } catch (IOException ex) {
            throw new Exception("Gagal menyimpan file " + randomFileName, ex);
        }
    }

    // Metode untuk mendapatkan file berdasarkan nama random
    public Resource loadFile(String fileName) throws Exception {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new Exception("File tidak ditemukan: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new Exception("File tidak ditemukan: " + fileName, ex);
        }
    }

    public void deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file: " + fileName, e);
        }
    }
}