package com.demo.gateway.gateway.response;

import com.demo.gateway.dto.InventoryReservationResultDto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Gateway-level response object for order operations.
 * Aggregates data from multiple backend services.
 */
@Data
@Builder
public class OrderGatewayResponse {
    // Order service response data
    private String orderId;
    private String status;
    private String message;
    private BigDecimal totalAmount;
    private LocalDate estimatedDeliveryDate;
    private LocalDateTime createdAt;

    // Inventory service response data
    private String reservationId;
    private boolean inventoryReserved;
    private List<InventoryReservationResultDto> inventoryResults;

    // Gateway metadata
    private boolean success;
    private String errorCode;
    private String errorMessage;
}
