package com.codeid.eshopay_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.codeid.eshopay_backend.model.dto.ApiResponsePagination;
import com.codeid.eshopay_backend.service.BaseCrudService;

import jakarta.validation.Valid;

public abstract class BaseCrudController<T, ID> {

    protected abstract BaseCrudService<T, ID> getService();

    @GetMapping
    public ResponseEntity<ApiResponsePagination<T>> getAll(@RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "asc") String sortingDirection) {

        ApiResponsePagination<T> response = getService().findAll(size, current, keyword, category, sortingDirection);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<T> getById(@PathVariable("id") ID id) {
        return ResponseEntity.ok(getService().findById(id));
    }

    @PostMapping
    public ResponseEntity<T> create(@RequestBody @Valid T entity) {
        return ResponseEntity.ok(getService().save(entity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<T> update(@PathVariable("id") ID id, @RequestBody @Valid T entity) {
        return ResponseEntity.ok(getService().update(id, entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") ID id) {
        getService().delete(id);
        return ResponseEntity.noContent().build();
    }
}