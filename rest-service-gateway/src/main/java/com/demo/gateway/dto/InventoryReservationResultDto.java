package com.demo.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReservationResultDto {
    private String productId;
    private int requestedQuantity;
    private int reservedQuantity;
    private String status;
    private String message;
}
