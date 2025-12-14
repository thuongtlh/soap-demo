package com.demo.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST DTO for Get Order Response.
 * This is what we return when retrieving order details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response payload containing order details")
public class GetOrderResponseDto {

    @Schema(description = "Order ID", example = "ORD-A1B2C3D4")
    private String orderId;

    @Schema(description = "Customer information")
    private CustomerDto customer;

    @Schema(description = "List of order items")
    private List<OrderItemDto> items;

    @Schema(description = "Order status", example = "CONFIRMED")
    private String status;

    @Schema(description = "Total order amount", example = "149.97")
    private BigDecimal totalAmount;

    @Schema(description = "Additional notes", example = "Please gift wrap")
    private String notes;

    @Schema(description = "Order creation timestamp", example = "2024-12-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2024-12-15T10:35:00")
    private LocalDateTime updatedAt;
}
