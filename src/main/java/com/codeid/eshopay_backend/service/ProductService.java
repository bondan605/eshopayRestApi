package com.codeid.eshopay_backend.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.codeid.eshopay_backend.model.dto.ProductDto;
import com.codeid.eshopay_backend.model.dto.ProductImageDto;

public interface ProductService extends BaseCrudService<ProductDto, Long> {

    List<ProductImageDto> bulkFindAll(Long id);

    List<ProductImageDto> bulkCreate(Long id, MultipartFile[] files, List<String> filenames);

}
