package com.demo.gateway.service;

import com.demo.gateway.dto.CreateOrderRequestDto;
import com.demo.gateway.dto.CreateOrderResponseDto;
import com.demo.gateway.gateway.OrderGateway;
import com.demo.gateway.gateway.request.OrderGatewayRequest;
import com.demo.gateway.gateway.request.OrderGatewayRequestBuilder;
import com.demo.gateway.gateway.response.OrderGatewayResponse;
import com.demo.gateway.gateway.response.OrderGatewayResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Order Service - business logic layer.
 * Delegates to Gateway for backend service orchestration.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderGateway orderGateway;
    private final OrderGatewayRequestBuilder gatewayRequestBuilder;
    private final OrderGatewayResponseBuilder gatewayResponseBuilder;

    /**
     * Create order with inventory reservation.
     */
    public CreateOrderResponseDto createOrder(CreateOrderRequestDto requestDto) {
        log.info("OrderService: Creating order for customer: {}", requestDto.getCustomer().getCustomerId());

        // Build gateway request from DTO
        OrderGatewayRequest gatewayRequest = gatewayRequestBuilder.fromDto(requestDto);

        // Call gateway to orchestrate backend services
        OrderGatewayResponse gatewayResponse = orderGateway.createOrderWithInventory(gatewayRequest);

        // Check for errors
        if (!gatewayResponse.isSuccess()) {
            throw new RuntimeException(gatewayResponse.getErrorMessage());
        }

        // Convert gateway response to DTO
        return gatewayResponseBuilder.toDto(gatewayResponse);
    }

    /**
     * Create order without inventory (simpler flow).
     */
    public CreateOrderResponseDto createOrderSimple(CreateOrderRequestDto requestDto) {
        log.info("OrderService: Creating simple order for customer: {}", requestDto.getCustomer().getCustomerId());

        OrderGatewayRequest gatewayRequest = gatewayRequestBuilder.fromDto(requestDto);
        OrderGatewayResponse gatewayResponse = orderGateway.createOrderOnly(gatewayRequest);

        if (!gatewayResponse.isSuccess()) {
            throw new RuntimeException(gatewayResponse.getErrorMessage());
        }

        return gatewayResponseBuilder.toDto(gatewayResponse);
    }
}
