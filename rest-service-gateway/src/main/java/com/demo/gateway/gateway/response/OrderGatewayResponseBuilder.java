package com.demo.gateway.gateway.response;

import com.demo.gateway.dto.CreateOrderResponseDto;
import com.demo.gateway.dto.InventoryReservationResultDto;
import com.demo.gateway.generated.inventory.ReservationResultType;
import com.demo.gateway.generated.inventory.ReserveInventoryResponse;
import com.demo.gateway.generated.order.CreateOrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Builder for creating gateway responses from SOAP service responses.
 * Handles aggregation and transformation of backend responses.
 */
@Slf4j
@Component
public class OrderGatewayResponseBuilder {

    /**
     * Build gateway response from order service response only.
     */
    public OrderGatewayResponse fromOrderResponse(CreateOrderResponse orderResponse) {
        return OrderGatewayResponse.builder()
                .orderId(orderResponse.getOrderId())
                .status(orderResponse.getStatus().value())
                .message(orderResponse.getMessage())
                .totalAmount(orderResponse.getTotalAmount())
                .estimatedDeliveryDate(toLocalDate(orderResponse.getEstimatedDeliveryDate()))
                .createdAt(toLocalDateTime(orderResponse.getCreatedAt()))
                .inventoryReserved(false)
                .inventoryResults(Collections.emptyList())
                .success(true)
                .build();
    }

    /**
     * Build gateway response from both order and inventory service responses.
     */
    public OrderGatewayResponse fromOrderAndInventoryResponse(
            CreateOrderResponse orderResponse,
            ReserveInventoryResponse inventoryResponse) {

        List<InventoryReservationResultDto> inventoryResults = inventoryResponse.getResults().stream()
                .map(this::toInventoryResultDto)
                .toList();

        return OrderGatewayResponse.builder()
                .orderId(orderResponse.getOrderId())
                .status(orderResponse.getStatus().value())
                .message(orderResponse.getMessage())
                .totalAmount(orderResponse.getTotalAmount())
                .estimatedDeliveryDate(toLocalDate(orderResponse.getEstimatedDeliveryDate()))
                .createdAt(toLocalDateTime(orderResponse.getCreatedAt()))
                .reservationId(inventoryResponse.getReservationId())
                .inventoryReserved(inventoryResponse.isAllReserved())
                .inventoryResults(inventoryResults)
                .success(true)
                .build();
    }

    /**
     * Build error response.
     */
    public OrderGatewayResponse errorResponse(String errorCode, String errorMessage) {
        return OrderGatewayResponse.builder()
                .success(false)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }

    /**
     * Convert gateway response to REST DTO.
     */
    public CreateOrderResponseDto toDto(OrderGatewayResponse gatewayResponse) {
        return CreateOrderResponseDto.builder()
                .orderId(gatewayResponse.getOrderId())
                .status(gatewayResponse.getStatus())
                .message(gatewayResponse.getMessage())
                .totalAmount(gatewayResponse.getTotalAmount())
                .estimatedDeliveryDate(gatewayResponse.getEstimatedDeliveryDate())
                .createdAt(gatewayResponse.getCreatedAt())
                .reservationId(gatewayResponse.getReservationId())
                .inventoryReserved(gatewayResponse.isInventoryReserved())
                .inventoryResults(gatewayResponse.getInventoryResults())
                .build();
    }

    private InventoryReservationResultDto toInventoryResultDto(ReservationResultType result) {
        return InventoryReservationResultDto.builder()
                .productId(result.getProductId())
                .requestedQuantity(result.getRequestedQuantity())
                .reservedQuantity(result.getReservedQuantity())
                .status(result.getStatus().value())
                .message(result.getMessage())
                .build();
    }

    private LocalDateTime toLocalDateTime(XMLGregorianCalendar calendar) {
        if (calendar == null) return null;
        return calendar.toGregorianCalendar().toZonedDateTime().toLocalDateTime();
    }

    private LocalDate toLocalDate(XMLGregorianCalendar calendar) {
        if (calendar == null) return null;
        return LocalDate.of(calendar.getYear(), calendar.getMonth(), calendar.getDay());
    }
}
