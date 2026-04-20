package com.pm.usersservice.exception;

import com.pm.usersservice.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException e,  HttpServletRequest request) {
        ApiError apiError = new ApiError();

        apiError.setStatus(HttpStatus.NOT_FOUND.value());
        apiError.setError("User Not Found");
        apiError.setMessage(e.getMessage());
        apiError.setPath(request.getRequestURI());
        apiError.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e,  HttpServletRequest request) {
        ApiError apiError = new ApiError();

        Map<String, String> fieldErrors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        apiError.setStatus(HttpStatus.BAD_REQUEST.value());
        apiError.setError("Validation Failed");
        apiError.setMessage(e.getMessage());
        apiError.setPath(request.getRequestURI());
        apiError.setValidationErrors(fieldErrors);
        apiError.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyExistsException(EmailAlreadyExistsException e,  HttpServletRequest request) {
        ApiError apiError = new ApiError();

        apiError.setStatus(HttpStatus.BAD_REQUEST.value());
        apiError.setError("Email already in use");
        apiError.setMessage(e.getMessage());
        apiError.setPath(request.getRequestURI());
        apiError.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
}
