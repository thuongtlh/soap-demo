package com.demo.gateway.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequestDto {
    @NotNull(message = "Customer is required")
    @Valid
    private CustomerDto customer;

    @NotEmpty(message = "At least one order item is required")
    @Valid
    private List<OrderItemDto> items;

    private String notes;

    @Builder.Default
    private Boolean priority = false;
}
