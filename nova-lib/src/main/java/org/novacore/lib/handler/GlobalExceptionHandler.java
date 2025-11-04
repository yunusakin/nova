package org.novacore.lib.handler;

import jakarta.validation.ConstraintViolationException;
import org.novacore.lib.api.ApiResponse;
import org.novacore.lib.exceptions.BusinessException;
import org.novacore.lib.exceptions.ResourceNotFoundException;
import org.novacore.lib.security.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.failure(ex.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.failure(ex.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthentication(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.failure(ex.getMessage()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(Exception ex) {
        var errors = switch (ex) {
            case MethodArgumentNotValidException manve -> manve.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> "%s: %s".formatted(error.getField(), error.getDefaultMessage()))
                    .collect(Collectors.joining(", "));
            case BindException be -> be.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> "%s: %s".formatted(error.getField(), error.getDefaultMessage()))
                    .collect(Collectors.joining(", "));
            default -> "Validation failure";
        };
        return ResponseEntity.badRequest().body(ApiResponse.failure(errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations()
                .stream()
                .map(violation -> "%s: %s".formatted(violation.getPropertyPath(), violation.getMessage()))
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(ApiResponse.failure(message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("Unexpected error: " + ex.getMessage()));
    }
}
