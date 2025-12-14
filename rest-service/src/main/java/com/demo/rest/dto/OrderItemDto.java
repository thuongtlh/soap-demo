package com.demo.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * REST DTO for Order Item information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order item information")
public class OrderItemDto {

    @NotBlank(message = "Product ID is required")
    @Schema(description = "Product identifier", example = "PROD-001")
    private String productId;

    @NotBlank(message = "Product name is required")
    @Schema(description = "Product name", example = "Wireless Headphones")
    private String productName;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Schema(description = "Quantity ordered", example = "2")
    private Integer quantity;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
    @Schema(description = "Price per unit", example = "49.99")
    private BigDecimal unitPrice;

    @Schema(description = "Total price (quantity * unitPrice)", example = "99.98")
    private BigDecimal totalPrice;
}
