package com.demo.rest.exception;

import com.demo.rest.dto.ErrorResponseDto;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.soap.SOAPFaultException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Global exception handler for REST API.
 *
 * Handles various exceptions and converts them to consistent error responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.warn("Validation error: {}", errors);

        ErrorResponseDto error = ErrorResponseDto.builder()
                .errorCode("VALIDATION_ERROR")
                .message(errors)
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handle SOAP fault exceptions (JAX-WS).
     */
    @ExceptionHandler(SOAPFaultException.class)
    public ResponseEntity<ErrorResponseDto> handleSoapFaultException(
            SOAPFaultException ex,
            WebRequest request) {

        log.error("SOAP fault: {}", ex.getMessage());

        String faultMessage = ex.getFault() != null ? ex.getFault().getFaultString() : ex.getMessage();

        ErrorResponseDto error = ErrorResponseDto.builder()
                .errorCode("SOAP_FAULT")
                .message("Error from backend service: " + faultMessage)
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }

    /**
     * Handle JAX-WS connection errors.
     */
    @ExceptionHandler(WebServiceException.class)
    public ResponseEntity<ErrorResponseDto> handleWebServiceException(
            WebServiceException ex,
            WebRequest request) {

        log.error("SOAP service error: {}", ex.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .errorCode("SERVICE_UNAVAILABLE")
                .message("Backend SOAP service is unavailable. Please try again later.")
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    /**
     * Handle all other exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception ex,
            WebRequest request) {

        log.error("Unexpected error: ", ex);

        ErrorResponseDto error = ErrorResponseDto.builder()
                .errorCode("INTERNAL_ERROR")
                .message("An unexpected error occurred")
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
