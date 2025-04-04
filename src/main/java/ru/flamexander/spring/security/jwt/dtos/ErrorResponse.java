package ru.flamexander.spring.security.jwt.dtos;

import lombok.Data;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class ErrorResponse {
    private List<String> errors;

    public ErrorResponse(List<ObjectError> errors) {
        this.errors = errors.stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());
    }
}
