package com.codeid.eshopay_backend.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codeid.eshopay_backend.model.dto.ProductDto;
import com.codeid.eshopay_backend.model.enumeration.EnumStatus;
import com.codeid.eshopay_backend.model.response.ApiResponse;
import com.codeid.eshopay_backend.service.BaseCrudService;
import com.codeid.eshopay_backend.service.FileStorageService;
import com.codeid.eshopay_backend.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/product")
@Slf4j
@RequiredArgsConstructor
public class ProductController extends BaseMultipartController<ProductDto, Long> {

    private final ProductService productService;
    private final FileStorageService fileStorageService;

    @Override
    protected BaseCrudService<ProductDto, Long> getService() {
        return productService;
    }

    @Override
    public ResponseEntity<ProductDto> create(@Valid ProductDto entity) {
        return super.create(entity);
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        return super.delete(id);
    }

    @Override
    public ResponseEntity<List<ProductDto>> getAll() {
        return super.getAll();
    }

    @Override
    public ResponseEntity<ProductDto> getById(Long id) {
        return super.getById(id);
    }

    @Override
    public ResponseEntity<ProductDto> update(Long id, @Valid ProductDto entity) {
        return super.update(id, entity);
    }

    @Override
    public ResponseEntity<?> createMultipart(ProductDto dto, MultipartFile file, String description) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload product photo");
        }
        try {
            String fileName = fileStorageService.storeFileWithRandomName(file);
            dto.setPhoto(fileName);
            var productDto = productService.save(dto);

            ApiResponse<ProductDto> response = new ApiResponse<ProductDto>(EnumStatus.Succeed.toString(),
                    "Product created", productDto);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> viewImage(String fileName) {
        try {
            Resource resource = fileStorageService.loadFile(fileName);

            // Cek jika file adalah image
            String contentType = determineContentType(fileName);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<?> updateMultipart(Long id, ProductDto dto, MultipartFile file, String description) {
        try {
            ProductDto existing = productService.findById(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Product not found"));
            }

            if (file != null && !file.isEmpty()) {
                // Hapus file lama kalau ada
                if (existing.getPhoto() != null && !existing.getPhoto().isEmpty()) {
                    fileStorageService.deleteFile(existing.getPhoto());
                }

                // Simpan file baru
                String fileName = fileStorageService.storeFileWithRandomName(file);
                dto.setPhoto(fileName);
            } else {
                // Pakai foto lama jika tidak upload baru
                dto.setPhoto(existing.getPhoto());
            }

            dto.setProductId(id); // Pastikan ID tetap

            ProductDto updated = productService.update(id, dto);

            return ResponseEntity.ok(new ApiResponse<>(
                    EnumStatus.Succeed.toString(),
                    "Product updated",
                    updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

}
