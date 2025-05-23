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
import com.codeid.eshopay_backend.model.dto.Pagination;
import com.codeid.eshopay_backend.model.dto.SuppliersDto;
import com.codeid.eshopay_backend.model.entity.Suppliers;
import com.codeid.eshopay_backend.repository.SuppliersRepository;
import com.codeid.eshopay_backend.service.SuppliersService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuppliersServiceImpl implements SuppliersService {
    private final SuppliersRepository suppliersRepository;

    public static SuppliersDto mapToDto(Suppliers suppliers) {
        return new SuppliersDto(
                suppliers.getSupplierId(),
                suppliers.getCompanyName());
    }

    public static Suppliers mapToEntity(SuppliersDto supplierDto) {
        return new Suppliers(
                supplierDto.getSuppliersId(),
                supplierDto.getCompanyName());
    }

    @Override
    public ApiResponsePagination<SuppliersDto> findAll(Integer size, Integer current, String keyword,
            String categoryName, String sortingDirection) {

        Pageable pageable = PageRequest.of(current - 1, size, Sort.by("supplierId").ascending());
        Page<Suppliers> pageResult = suppliersRepository.findAll(pageable);
        List<SuppliersDto> supplierDTOs = pageResult.getContent().stream().map(SuppliersServiceImpl::mapToDto)
                .collect(Collectors.toList());

        Pagination pagination = new Pagination();
        pagination.setCurrent(current);
        pagination.setSize(size);
        pagination.setTotal(pageResult.getTotalElements());
        pagination.setTotalPages(pageResult.getTotalPages());

        ApiResponsePagination<SuppliersDto> response = new ApiResponsePagination<>();
        response.setMessage("success get data");
        response.setStatusCode(200);
        response.setTimestamp(LocalDateTime.now());
        response.setPage(pagination);
        response.setData(supplierDTOs);

        return response;

    }

    @Override
    public SuppliersDto findById(Long id) {
        log.debug("Request to get supplier : {}", id);

        return this.suppliersRepository.findById(id).map(SuppliersServiceImpl::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("supplier not found with id " + id));
    }

    @Override
    public SuppliersDto save(SuppliersDto entity) {
        log.debug("Request to create supplier : {}", entity);

        return mapToDto(this.suppliersRepository
                .save(new Suppliers(entity.getCompanyName())));
    }

    @Override
    public SuppliersDto update(Long id, SuppliersDto entity) {
        log.debug("Request to update Supplier : {}", id);

        var suppliers = this.suppliersRepository
                .findById(id)
                .orElse(null);

        suppliers.setCompanyName(entity.getCompanyName());
        this.suppliersRepository.save(suppliers);
        return mapToDto(suppliers);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Supplier : {}", id);

        var suppliers = this.suppliersRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Cannot find Supplier with id " + id));

        this.suppliersRepository.delete(suppliers);
    }
}
