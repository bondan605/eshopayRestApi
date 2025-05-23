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
import com.codeid.eshopay_backend.model.dto.ShippersDto;
import com.codeid.eshopay_backend.model.entity.Shippers;
import com.codeid.eshopay_backend.repository.ShippersRepository;
import com.codeid.eshopay_backend.service.ShippersService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShippersServiceImpl implements ShippersService {
    private final ShippersRepository shippersRepository;

    public static ShippersDto mapToDto(Shippers shippers) {
        return new ShippersDto(
                shippers.getShipperId(),
                shippers.getCompanyName(),
                shippers.getPhone());
    }

    @Override
    public ApiResponsePagination<ShippersDto> findAll(Integer size, Integer current, String keyword,
            String categoryName, String sortingDirection) {

        Pageable pageable = PageRequest.of(current - 1, size, Sort.by("shipperId"));
        Page<Shippers> pageResult = shippersRepository.findAll(pageable);
        List<ShippersDto> shippersDTOs = pageResult.getContent().stream().map(ShippersServiceImpl::mapToDto)
                .collect(Collectors.toList());

        Pagination pagination = new Pagination();
        pagination.setSize(size);
        pagination.setCurrent(current);
        pagination.setTotal(pageResult.getTotalElements());
        pagination.setTotalPages(pageResult.getTotalPages());

        ApiResponsePagination<ShippersDto> response = new ApiResponsePagination<>();
        response.setMessage("success get data");
        response.setPage(pagination);
        response.setStatusCode(200);
        response.setTimestamp(LocalDateTime.now());
        response.setData(shippersDTOs);

        return response;
    }

    @Override
    public ShippersDto findById(Long id) {
        log.debug("Request to get shipper : {}", id);

        return this.shippersRepository.findById(id).map(ShippersServiceImpl::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("shipper not found with id " + id));
    }

    @Override
    public ShippersDto save(ShippersDto entity) {
        log.debug("Request to create shipper : {}", entity);

        Shippers shippers = new Shippers();
        shippers.setCompanyName(entity.getCompanyName());
        shippers.setPhone(entity.getPhone());

        return mapToDto(this.shippersRepository.save(shippers));
    }

    @Override
    public ShippersDto update(Long id, ShippersDto entity) {
        log.debug("Request to update shipper : {}", id);

        var shippers = this.shippersRepository
                .findById(id)
                .orElse(null);

        shippers.setCompanyName(entity.getCompanyName());
        shippers.setPhone(entity.getPhone());
        this.shippersRepository.save(shippers);
        return mapToDto(shippers);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete shipper : {}", id);

        var shippers = this.shippersRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Cannot find shipper with id " + id));

        this.shippersRepository.delete(shippers);
    }
}
