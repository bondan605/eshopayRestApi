package com.codeid.eshopay_backend.service.implementation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.codeid.eshopay_backend.model.dto.ApiResponsePagination;
import com.codeid.eshopay_backend.model.dto.CategoryDto;
import com.codeid.eshopay_backend.model.dto.Pagination;
import com.codeid.eshopay_backend.model.entity.Category;
import com.codeid.eshopay_backend.repository.CategoryRepository;
import com.codeid.eshopay_backend.service.CategoryService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public static CategoryDto mapToDto(Category category) {
        return new CategoryDto(category.getCategoryId(), category.getCategoryName(), category.getDescription());
    }

    public static Category mapToEntity(CategoryDto categoryDto) {
        return new Category(
                categoryDto.getCategoryId(),
                categoryDto.getCategoryName(),
                categoryDto.getDescription());
    }

    @Override
    public ApiResponsePagination<CategoryDto> findAll(Integer size, Integer current, String keyword,
            String categoryName, String sortingDirection) {

        Pageable pageable = PageRequest.of(current - 1, size, Sort.by("categoryId").ascending());
        Page<Category> pageResult = categoryRepository.findAll(pageable);
        List<CategoryDto> categoryDTOs = pageResult.getContent().stream().map(CategoryServiceImpl::mapToDto)
                .collect(Collectors.toList());
        Pagination pagination = new Pagination();
        pagination.setSize(size);
        pagination.setCurrent(current);
        pagination.setTotal(pageResult.getTotalElements());
        pagination.setTotalPages(pageResult.getTotalPages());

        ApiResponsePagination<CategoryDto> response = new ApiResponsePagination<>();
        response.setMessage("success get category");
        response.setTimestamp(LocalDateTime.now());
        response.setStatusCode(200);
        response.setData(categoryDTOs);
        response.setPage(pagination);
        return response;
    }

    @Override
    public CategoryDto findById(Long id) {
        return this.categoryRepository.findById(id).map(CategoryServiceImpl::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id " + id));
    }

    @Override
    public CategoryDto save(CategoryDto entity) {
        Category category = new Category();
        category.setCategoryName(entity.getCategoryName());
        category.setDescription(entity.getDescription());
        return mapToDto(this.categoryRepository.save(category));
    }

    @Override
    public CategoryDto update(Long id, CategoryDto entity) {
        var category = this.categoryRepository
                .findById(id)
                .orElse(null);
        category.setCategoryName(entity.getCategoryName());
        category.setDescription(entity.getDescription());
        this.categoryRepository.save(category);
        return mapToDto(category);
    }

    @Override
    public void delete(Long id) {
        var category = this.categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Cannot find Category with id " + id));
        this.categoryRepository.delete(category);
    }

}
