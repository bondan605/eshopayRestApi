package com.codeid.eshopay_backend.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponsePagination<T> {

    private String message;
    private LocalDateTime timestamp;
    private int statusCode;
    private List<T> data;
    private Pagination page;

}