package com.demo.gateway.gateway;

import com.demo.gateway.client.InventorySoapClient;
import com.demo.gateway.client.OrderSoapClient;
import com.demo.gateway.gateway.request.InventorySoapRequestBuilder;
import com.demo.gateway.gateway.request.OrderGatewayRequest;
import com.demo.gateway.gateway.request.OrderGatewayRequestBuilder;
import com.demo.gateway.gateway.request.OrderSoapRequestBuilder;
import com.demo.gateway.gateway.response.OrderGatewayResponse;
import com.demo.gateway.gateway.response.OrderGatewayResponseBuilder;
import com.demo.gateway.generated.inventory.ReserveInventoryRequest;
import com.demo.gateway.generated.inventory.ReserveInventoryResponse;
import com.demo.gateway.generated.order.CreateOrderRequest;
import com.demo.gateway.generated.order.CreateOrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Order Gateway - orchestrates calls to multiple backend SOAP services.
 *
 * This gateway:
 * 1. Receives gateway-level requests
 * 2. Validates business rules
 * 3. Calls multiple backend services (Order + Inventory)
 * 4. Aggregates responses
 * 5. Returns unified gateway response
 *
 * Benefits of Gateway pattern:
 * - Centralized orchestration logic
 * - Request/response transformation
 * - Cross-cutting concerns (logging, resilience)
 * - Aggregation from multiple services
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderGateway {

    private final OrderSoapClient orderSoapClient;
    private final InventorySoapClient inventorySoapClient;
    private final OrderGatewayRequestBuilder gatewayRequestBuilder;
    private final OrderSoapRequestBuilder orderSoapRequestBuilder;
    private final InventorySoapRequestBuilder inventorySoapRequestBuilder;
    private final OrderGatewayResponseBuilder gatewayResponseBuilder;

    /**
     * Create order with inventory reservation.
     *
     * Flow:
     * 1. Create order in Order Service
     * 2. Reserve inventory in Inventory Service
     * 3. Aggregate and return combined response
     */
    public OrderGatewayResponse createOrderWithInventory(OrderGatewayRequest gatewayRequest) {
        log.info("Gateway: Creating order with inventory for customer: {}",
                gatewayRequest.getCustomer().getCustomerId());

        try {
            // Step 1: Create order in Order Service
            CreateOrderRequest orderRequest = orderSoapRequestBuilder.buildCreateOrderRequest(gatewayRequest);
            CreateOrderResponse orderResponse = orderSoapClient.createOrder(orderRequest);

            log.info("Gateway: Order created with ID: {}", orderResponse.getOrderId());

            // Step 2: Reserve inventory in Inventory Service
            ReserveInventoryRequest inventoryRequest = inventorySoapRequestBuilder
                    .buildReserveInventoryRequest(orderResponse.getOrderId(), gatewayRequest.getItems());
            ReserveInventoryResponse inventoryResponse = inventorySoapClient.reserveInventory(inventoryRequest);

            log.info("Gateway: Inventory reserved: {}, allReserved: {}",
                    inventoryResponse.getReservationId(), inventoryResponse.isAllReserved());

            // Step 3: Aggregate responses
            OrderGatewayResponse response = gatewayResponseBuilder
                    .fromOrderAndInventoryResponse(orderResponse, inventoryResponse);

            log.info("Gateway: Order creation completed successfully. OrderId: {}, ReservationId: {}",
                    response.getOrderId(), response.getReservationId());

            return response;

        } catch (Exception e) {
            log.error("Gateway: Error creating order: {}", e.getMessage(), e);
            return gatewayResponseBuilder.errorResponse("ORDER_CREATION_FAILED", e.getMessage());
        }
    }

    /**
     * Create order without inventory reservation.
     * Useful when inventory check is optional or handled separately.
     */
    public OrderGatewayResponse createOrderOnly(OrderGatewayRequest gatewayRequest) {
        log.info("Gateway: Creating order (without inventory) for customer: {}",
                gatewayRequest.getCustomer().getCustomerId());

        try {
            CreateOrderRequest orderRequest = orderSoapRequestBuilder.buildCreateOrderRequest(gatewayRequest);
            CreateOrderResponse orderResponse = orderSoapClient.createOrder(orderRequest);

            return gatewayResponseBuilder.fromOrderResponse(orderResponse);

        } catch (Exception e) {
            log.error("Gateway: Error creating order: {}", e.getMessage(), e);
            return gatewayResponseBuilder.errorResponse("ORDER_CREATION_FAILED", e.getMessage());
        }
    }
}
