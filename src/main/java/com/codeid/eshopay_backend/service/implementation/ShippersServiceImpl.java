package com.codeid.eshopay_backend.service.implementation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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
    public List<ShippersDto> findAll() {
        log.debug("request fetching data shipper");
        return this.shippersRepository.findAll()
                .stream()
                .map(ShippersServiceImpl::mapToDto) // untuk ubah tipe data dari department ke departmentDto
                .collect(Collectors.toList());
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
