package ru.flamexander.spring.security.jwt.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        if (ex.getRequiredType() == Long.class) {
            return "redirect:/error?message=Invalid ID format";
        }
        return "redirect:/error";
    }
    @ExceptionHandler(UserNotFoundException.class)
    public AppError handleUserNotFoundException(UserNotFoundException ex) {
        return new AppError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }
}
