package com.demo.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * REST DTO for Create Order Response.
 * This is what we return to the REST client after processing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response payload after creating an order")
public class CreateOrderResponseDto {

    @Schema(description = "Generated order ID", example = "ORD-A1B2C3D4")
    private String orderId;

    @Schema(description = "Order status", example = "CONFIRMED")
    private String status;

    @Schema(description = "Status message", example = "Order successfully created and confirmed")
    private String message;

    @Schema(description = "Total order amount", example = "149.97")
    private BigDecimal totalAmount;

    @Schema(description = "Estimated delivery date", example = "2024-12-20")
    private LocalDate estimatedDeliveryDate;

    @Schema(description = "Order creation timestamp", example = "2024-12-15T10:30:00")
    private LocalDateTime createdAt;
}
