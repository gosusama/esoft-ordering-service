package com.esoft.common.config.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.NoPermissionException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException ex) {
        ApiResponse error = new ApiResponse("error", ex.getMessage());

        // set message from custom validator
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            error.setMessage(fieldError.getDefaultMessage());
        }

        // return ResponseEntity
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(NoPermissionException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ApiResponse> handleNoPermissionException(NoPermissionException ex) {
        ApiResponse error = new ApiResponse("error", ex.getMessage());

        // return ResponseEntity
        return ResponseEntity.status(401).body(error);
    }

    // handle exception when order not found
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse> handleException(OrderNotFoundException ex) {
        ApiResponse error = new ApiResponse("error", ex.getMessage());

        // return ResponseEntity
        return ResponseEntity.badRequest().body(error);
    }

    // handle other exception
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse> handleException(Exception ex) {
        ApiResponse error = new ApiResponse("error", ex.getMessage());

        // return ResponseEntity
        return ResponseEntity.badRequest().body(error);
    }
}
