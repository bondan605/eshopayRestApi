package com.codeid.eshopay_backend.service.implementation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.codeid.eshopay_backend.model.dto.CategoryDto;
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
    public List<CategoryDto> findAll() {
        return this.categoryRepository.findAll()
                .stream()
                .map(CategoryServiceImpl::mapToDto)
                .collect(Collectors.toList());
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
