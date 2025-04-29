package ru.flamexander.spring.security.jwt.exceptions;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) { super(message); }
}