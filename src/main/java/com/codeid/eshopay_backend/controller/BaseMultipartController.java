package com.codeid.eshopay_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

public abstract class BaseMultipartController<T, ID> extends BaseCrudController<T, ID> {

    @PostMapping(consumes = { "multipart/form-data" }, value = "/upload")
    public abstract ResponseEntity<?> createMultipart(
            @RequestPart("data") T dto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "description", required = false) String description);

    @GetMapping("/view/{fileName:.+}")
    public abstract ResponseEntity<?> viewImage(@PathVariable("fileName") String fileName);

    public String determineContentType(String fileName) {
        // Implementasi sederhana - bisa diganti dengan cara yang lebih robust
        if (fileName.toLowerCase().endsWith(".png")) {
            return "image/png";
        } else if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.toLowerCase().endsWith(".gif")) {
            return "image/gif";
        }
        return "application/octet-stream";
    }

    @PutMapping(value = "/{id}/upload", consumes = { "multipart/form-data" })
    public abstract ResponseEntity<?> updateMultipart(
            @PathVariable("id") ID id,
            @RequestPart("data") T dto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "description", required = false) String description);
}
