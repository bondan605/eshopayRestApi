package com.codeid.eshopay_backend.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codeid.eshopay_backend.model.dto.ProductDto;
import com.codeid.eshopay_backend.model.dto.ProductImageDto;
import com.codeid.eshopay_backend.model.enumeration.EnumStatus;
import com.codeid.eshopay_backend.model.response.ApiResponse;
import com.codeid.eshopay_backend.service.BaseCrudService;
import com.codeid.eshopay_backend.service.FileStorageService;
import com.codeid.eshopay_backend.service.ProductService;
import com.codeid.eshopay_backend.util.SuccessMessage;

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
    public ResponseEntity<?> createMultipart(ProductDto dto, MultipartFile file, String description) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload product photo");
        }
        try {
            String fileName = fileStorageService.storeFileWithRandomName(file);
            dto.setPhoto(fileName);
            var productDto = productService.save(dto);

            ApiResponse<ProductDto> apiResponse = new ApiResponse<>(
                    SuccessMessage.UploadImage.UPLOAD_IMAGE, productDto, LocalDateTime.now(), SuccessMessage.Http.OK);
            return ResponseEntity.ok(apiResponse);

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

            ApiResponse<ProductDto> apiResponse = new ApiResponse<>(
                    SuccessMessage.Update.Update_Multipart, updated, LocalDateTime.now(), SuccessMessage.Http.OK);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/uploadMultipleImages")
    public ResponseEntity<?> getAllMultipartBulk(
            @PathVariable("id") Long id) {
        var response = ApiResponse.builder()
                .message("success get photo")
                .data(productService.bulkFindAll(id))
                .statusCode(200)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/{id}/uploadMultipleImages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createMultipartBulk(
            @PathVariable("id") Long id,
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            @RequestParam(value = "description", required = false) String description) {

        if (files == null || files.length == 0) {
            return ResponseEntity.badRequest().body("Please upload product images");
        }

        try {
            List<String> filenames = Arrays.stream(files)
                    .map(file -> {
                        try {
                            return fileStorageService.storeFileWithRandomName(file);
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to store file: " + file.getOriginalFilename(), e);
                        }
                    })
                    .toList();
            List<ProductImageDto> productImagesDto = productService.bulkCreate(id, files, filenames);

            ApiResponse<Object> response = ApiResponse.builder()
                    .message("product photos created")
                    .statusCode(200)
                    .data(productImagesDto)
                    .timestamp(LocalDateTime.now())
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonMap("error", ex.getMessage()));
        }
    }

}
