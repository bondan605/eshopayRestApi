package com.codeid.eshopay_backend.service.implementation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.codeid.eshopay_backend.model.dto.ProductDto;
import com.codeid.eshopay_backend.model.dto.ProductImageDto;
import com.codeid.eshopay_backend.model.entity.Category;
import com.codeid.eshopay_backend.model.entity.Product;
import com.codeid.eshopay_backend.model.entity.ProductImage;
import com.codeid.eshopay_backend.model.entity.Suppliers;
import com.codeid.eshopay_backend.repository.ProductImageRepository;
import com.codeid.eshopay_backend.repository.ProductRepository;
import com.codeid.eshopay_backend.service.ProductService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

        private final ProductImageRepository productImageRepository;
        private final ProductRepository productRepository;

        public static ProductDto mapToDto(Product product) {
                return new ProductDto(
                                product.getProductId(), product.getProductName(),
                                product.getQuantityPerUnit(), product.getUnitPrice(),
                                product.getUnitsInStock(), product.getUnitsOnOrder(),
                                product.getReorderLevel(), product.getDiscontinued(),
                                product.getPhoto(),
                                SuppliersServiceImpl.mapToDto(product.getSupplier()),
                                CategoryServiceImpl.mapToDto(product.getCategory()),
                                product.getProductImages());
        }

        public static ProductImageDto mapToImageDto(ProductImage productImage) {
                return new ProductImageDto(
                                productImage.getImageId(),
                                productImage.getFileName(),
                                productImage.getFileSize(),
                                productImage.getFileType(),
                                productImage.getFileUri(),
                                productImage.getProduct().getProductId());
        }

        private Product mapToEntity(ProductDto productDto) {
                return new Product(
                                productDto.getProductId(), productDto.getProductName(),
                                productDto.getQuantityPerUnit(), productDto.getUnitPrice(),
                                productDto.getUnitsInStock(), productDto.getUnitsOnOrder(),
                                productDto.getReorderLevel(), productDto.getDiscontinued(),
                                productDto.getPhoto(),
                                SuppliersServiceImpl.mapToEntity(productDto.getSupplier()),
                                CategoryServiceImpl.mapToEntity(productDto.getCategory()),
                                productDto.getProductImages());
        }

        @Override
        public List<ProductDto> findAll() {
                log.debug("Request to get all Products");
                return this.productRepository.findAll().stream()
                                .map(ProductServiceImpl::mapToDto)
                                .collect(Collectors.toList());
        }

        @Override
        public ProductDto findById(Long id) {
                log.debug("Request to get Product : {}", id);
                return this.productRepository.findById(id).map(ProductServiceImpl::mapToDto)
                                .orElseThrow(() -> new EntityNotFoundException("Product not found with id " + id));
        }

        @Override
        public ProductDto save(ProductDto entity) {
                log.debug("Request to create Product : {}", entity);

                var product = mapToEntity(entity);

                // after save, langsung ubah ke dto
                return mapToDto(this.productRepository.save(product));
        }

        @Override
        public ProductDto update(Long id, ProductDto entity) {
                log.debug("Request to update Product : {}", id);
                var product = this.productRepository
                                .findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Product not found with id " + id));

                product.setProductName(entity.getProductName());
                product.setQuantityPerUnit(entity.getQuantityPerUnit());
                product.setUnitPrice(entity.getUnitPrice());
                product.setUnitsInStock(entity.getUnitsInStock());
                product.setUnitsOnOrder(entity.getUnitsOnOrder());
                product.setReorderLevel(entity.getReorderLevel());
                product.setDiscontinued(entity.getDiscontinued());
                product.setPhoto(entity.getPhoto());
                product.setSupplier(
                                new Suppliers(entity.getSupplier().getSuppliersId(),
                                                entity.getSupplier().getCompanyName()));
                product.setCategory(new Category(entity.getCategory().getCategoryId(),
                                entity.getCategory().getCategoryName(),
                                entity.getCategory().getDescription()));

                this.productRepository.save(product);
                return mapToDto(product);
        }

        @Override
        public void delete(Long id) {
                log.debug("Request to delete product : {}", id);

                var product = this.productRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Product not found with id " + id));

                this.productRepository.deleteById(product.getProductId());
        }

        @Override
        public List<ProductImageDto> bulkFindAll(Long id) {
                Product product = this.productRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Product not found with id " + id));

                List<ProductImage> productImages = this.productImageRepository.findAllByProduct(product);

                return productImages.stream()
                                .map(ProductServiceImpl::mapToImageDto)
                                .toList();
        }

        @Transactional
        @Override
        public List<ProductImageDto> bulkCreate(Long id, MultipartFile[] files, List<String> filenames) {
                Product product = this.productRepository.findById(id)
                                .orElseThrow(
                                                () -> new EntityNotFoundException("Product not found with id " + id));

                this.productImageRepository.deleteByProduct(product);

                List<ProductImage> productImages = IntStream.range(0, files.length)
                                .mapToObj(i -> {
                                        ProductImage img = new ProductImage();
                                        img.setFileName(filenames.get(i));
                                        img.setFileSize(files[i].getSize());
                                        img.setFileType(files[i].getContentType());
                                        img.setFileUri("http://localhost:8088/api/product/view/" + filenames.get(i));
                                        img.setProduct(product);
                                        return img;
                                })
                                .toList();

                this.productImageRepository.saveAll(productImages);

                return productImages.stream()
                                .map(ProductServiceImpl::mapToImageDto)
                                .toList();
        }

}
