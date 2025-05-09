package com.codeid.eshopay_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codeid.eshopay_backend.model.dto.SuppliersDto;
import com.codeid.eshopay_backend.service.BaseCrudService;
import com.codeid.eshopay_backend.service.SuppliersService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/supplier")
@RequiredArgsConstructor
public class SuppliersController extends BaseCrudController<SuppliersDto, Long> {
    private final SuppliersService suppliersService;

    @Override
    protected BaseCrudService<SuppliersDto, Long> getService() {
        return suppliersService;
    }

    @Override
    public ResponseEntity<SuppliersDto> create(@Valid SuppliersDto entity) {
        return super.create(entity);
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        return super.delete(id);
    }

    @Override
    public ResponseEntity<List<SuppliersDto>> getAll() {
        return super.getAll();
    }

    @Override
    public ResponseEntity<SuppliersDto> getById(Long id) {
        return super.getById(id);
    }

    @Override
    public ResponseEntity<SuppliersDto> update(Long id, @Valid SuppliersDto entity) {
        return super.update(id, entity);
    }
}