package com.codeid.eshopay_backend.service.implementation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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
    public List<SuppliersDto> findAll() {
        log.debug("request fetching data categories");
        return this.suppliersRepository.findAll()
                .stream()
                .map(SuppliersServiceImpl::mapToDto) // untuk ubah tipe data dari department ke departmentDto
                .collect(Collectors.toList());
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
