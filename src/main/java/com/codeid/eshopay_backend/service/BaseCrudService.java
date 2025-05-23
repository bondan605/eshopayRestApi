package com.codeid.eshopay_backend.service;

import com.codeid.eshopay_backend.model.dto.ApiResponsePagination;

public interface BaseCrudService<T, ID> {
    // List<T> findAll();

    ApiResponsePagination<T> findAll(Integer size, Integer current, String keyword, String category,
            String sortingDirection);

    T findById(ID id);

    T save(T entity);

    T update(ID id, T entity);

    void delete(ID id);
}
