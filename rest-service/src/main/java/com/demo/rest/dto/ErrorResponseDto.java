package com.demo.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * REST DTO for Error Response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response payload")
public class ErrorResponseDto {

    @Schema(description = "Error code", example = "ORDER_NOT_FOUND")
    private String errorCode;

    @Schema(description = "Error message", example = "Order not found: ORD-12345")
    private String message;

    @Schema(description = "Error timestamp", example = "2024-12-15T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Request path", example = "/api/v1/orders/ORD-12345")
    private String path;
}
