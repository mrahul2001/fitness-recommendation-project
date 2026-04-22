package com.pm.activitiesservice.exception;

import com.pm.activitiesservice.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ActivityNotFoundException.class)
    public ResponseEntity<ApiError> handleActivityNotFoundException(ActivityNotFoundException e,  HttpServletRequest request) {
        ApiError apiError = new ApiError();

        apiError.setStatus(HttpStatus.NOT_FOUND.value());
        apiError.setError("Activity Not Found");
        apiError.setMessage(e.getMessage());
        apiError.setPath(request.getRequestURI());
        apiError.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

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
}
