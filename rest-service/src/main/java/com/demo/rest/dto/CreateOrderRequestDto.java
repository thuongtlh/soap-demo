package com.demo.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * REST DTO for Create Order Request.
 * This is what the REST client sends to our API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating a new order")
public class CreateOrderRequestDto {

    @NotNull(message = "Customer is required")
    @Valid
    @Schema(description = "Customer information")
    private CustomerDto customer;

    @NotEmpty(message = "At least one item is required")
    @Valid
    @Schema(description = "List of order items")
    private List<OrderItemDto> items;

    @Schema(description = "Additional notes for the order", example = "Please gift wrap")
    private String notes;

    @Schema(description = "Priority shipping flag", example = "false", defaultValue = "false")
    private boolean priority;
}
