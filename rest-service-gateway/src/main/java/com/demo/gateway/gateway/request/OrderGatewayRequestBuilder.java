package com.demo.gateway.gateway.request;

import com.demo.gateway.dto.CreateOrderRequestDto;
import com.demo.gateway.dto.CustomerDto;
import com.demo.gateway.dto.OrderItemDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Builder for creating OrderGatewayRequest from various sources.
 * Handles validation and transformation of input data to gateway format.
 */
@Slf4j
@Component
public class OrderGatewayRequestBuilder {

    /**
     * Build gateway request from REST DTO.
     */
    public OrderGatewayRequest fromDto(CreateOrderRequestDto dto) {
        log.debug("Building OrderGatewayRequest from DTO for customer: {}", dto.getCustomer().getCustomerId());

        return OrderGatewayRequest.builder()
                .customer(dto.getCustomer())
                .items(dto.getItems())
                .notes(dto.getNotes())
                .priority(Boolean.TRUE.equals(dto.getPriority()))
                .inventoryChecked(false)
                .inventoryAvailable(false)
                .build();
    }

    /**
     * Create a new request with enriched customer data.
     */
    public OrderGatewayRequest withEnrichedCustomer(OrderGatewayRequest request, CustomerDto enrichedCustomer) {
        return OrderGatewayRequest.builder()
                .customer(enrichedCustomer)
                .items(request.getItems())
                .notes(request.getNotes())
                .priority(request.isPriority())
                .inventoryChecked(request.isInventoryChecked())
                .inventoryAvailable(request.isInventoryAvailable())
                .build();
    }

    /**
     * Create a new request with inventory check results.
     */
    public OrderGatewayRequest withInventoryStatus(OrderGatewayRequest request, boolean available) {
        return OrderGatewayRequest.builder()
                .customer(request.getCustomer())
                .items(request.getItems())
                .notes(request.getNotes())
                .priority(request.isPriority())
                .inventoryChecked(true)
                .inventoryAvailable(available)
                .build();
    }

    /**
     * Extract product IDs from the request for inventory checking.
     */
    public List<String> extractProductIds(OrderGatewayRequest request) {
        return request.getItems().stream()
                .map(OrderItemDto::getProductId)
                .toList();
    }
}
