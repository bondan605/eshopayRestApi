package com.codeid.eshopay_backend.service.implementation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.codeid.eshopay_backend.exception.InsunfficientStockProduct;
import com.codeid.eshopay_backend.exception.ProductNotFoundException;
import com.codeid.eshopay_backend.model.dto.ApiResponsePagination;
import com.codeid.eshopay_backend.model.dto.Filter;
import com.codeid.eshopay_backend.model.dto.Pagination;
import com.codeid.eshopay_backend.model.dto.ProductDto;
import com.codeid.eshopay_backend.model.dto.ProductImageDto;
import com.codeid.eshopay_backend.model.entity.Category;
import com.codeid.eshopay_backend.model.entity.Product;
import com.codeid.eshopay_backend.model.entity.ProductImage;
import com.codeid.eshopay_backend.model.entity.Suppliers;
import com.codeid.eshopay_backend.repository.ProductImageRepository;
import com.codeid.eshopay_backend.repository.ProductRepository;
import com.codeid.eshopay_backend.repository.specs.ProductItemSpec;
import com.codeid.eshopay_backend.service.ProductService;
import com.codeid.eshopay_backend.util.ErrorMessage;
import com.codeid.eshopay_backend.util.SuccessMessage;

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
                                CategoryServiceImpl.mapToDto(product.getCategory()));
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

        public static Product mapToEntity(ProductDto productDto) {
                return new Product(
                                productDto.getProductId(), productDto.getProductName(),
                                productDto.getQuantityPerUnit(), productDto.getUnitPrice(),
                                productDto.getUnitsInStock(), productDto.getUnitsOnOrder(),
                                productDto.getReorderLevel(), productDto.getDiscontinued(),
                                productDto.getPhoto(),
                                SuppliersServiceImpl.mapToEntity(productDto.getSupplierDto()),
                                CategoryServiceImpl.mapToEntity(productDto.getCategoryDto()));
        }

        @Override
        public ApiResponsePagination<ProductDto> findAll(Integer size, Integer current, String keyword, String category,
                        String sortingDirection) {

                // searching
                Specification<Product> specs = ProductItemSpec.searchSpecification(keyword, category);

                // Sorting
                Sort sort;
                switch (sortingDirection.toLowerCase()) {
                        case "price_asc":
                                sort = Sort.by("unitPrice").ascending();
                                break;
                        case "price_desc":
                                sort = Sort.by("unitPrice").descending();
                                break;
                        case "desc":
                                sort = Sort.by("productId").descending();
                                break;
                        case "asc":
                        default:
                                sort = Sort.by("productId").ascending();
                                break;
                }

                // get all data
                Pageable pageable = PageRequest.of(current - 1, size, sort);
                Page<Product> pageResult = productRepository.findAll(specs, pageable);
                List<ProductDto> productDTOs = pageResult.getContent().stream().map(ProductServiceImpl::mapToDto)
                                .collect(Collectors.toList());

                Pagination pagination = new Pagination();
                pagination.setCurrent(current);
                pagination.setSize(size);
                pagination.setTotal(pageResult.getTotalElements());
                pagination.setTotalPages(pageResult.getTotalPages());
                pagination.setFilter(
                                Filter.builder()
                                                .keyword(keyword)
                                                .category(category)
                                                .build());

                ApiResponsePagination<ProductDto> response = new ApiResponsePagination<>();
                response.setMessage(SuccessMessage.FindAll.FIND_DATA);
                response.setStatusCode(200);
                response.setData(productDTOs);
                response.setTimestamp(LocalDateTime.now());
                response.setPage(pagination);

                return response;
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
                                new Suppliers(entity.getSupplierDto().getSuppliersId(),
                                                entity.getSupplierDto().getCompanyName()));
                product.setCategory(new Category(entity.getCategoryDto().getCategoryId(),
                                entity.getCategoryDto().getCategoryName(),
                                entity.getCategoryDto().getDescription()));

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

        @Override
        @Transactional
        public void updateProductStock(Long productId, Integer quantity) {
                Product products = productRepository.findById(productId)
                                .orElseThrow(() -> new EntityNotFoundException("product id not found " + productId));

                int newStock = products.getUnitsInStock() - quantity;
                if (newStock < 0) {
                        throw new ProductNotFoundException(
                                        ErrorMessage.Product.PRODUCT_STOCK_INSUNFFICIENT + productId);
                }
                products.setUnitsInStock(newStock);
                productRepository.save(products);
        }

}
