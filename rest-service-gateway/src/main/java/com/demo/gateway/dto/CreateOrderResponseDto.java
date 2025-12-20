package com.demo.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderResponseDto {
    private String orderId;
    private String status;
    private String message;
    private BigDecimal totalAmount;
    private LocalDate estimatedDeliveryDate;
    private LocalDateTime createdAt;

    // Inventory reservation details (from Gateway aggregation)
    private String reservationId;
    private boolean inventoryReserved;
    private List<InventoryReservationResultDto> inventoryResults;
}
