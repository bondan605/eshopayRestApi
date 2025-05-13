package com.codeid.eshopay_backend.model.response;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiError {
    private HttpStatus status;
    private String message;
    private List<String> errors;
    private LocalDateTime timestamp;

    // Constructors
    public ApiError(HttpStatus status, String message, List<String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }

}
