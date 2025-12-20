package com.demo.gateway.client;

import com.demo.gateway.generated.inventory.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * SOAP Client for Inventory Service with resilience patterns.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InventorySoapClient {

    private final InventoryPort inventoryPort;

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "checkInventoryFallback")
    @Retry(name = "inventoryService")
    public CheckInventoryResponse checkInventory(CheckInventoryRequest request) {
        log.info("Calling Inventory SOAP service - checkInventory for {} products",
                request.getProductIds().size());

        CheckInventoryResponse response = inventoryPort.checkInventory(request);

        log.info("Inventory SOAP service responded with {} items", response.getItems().size());
        return response;
    }

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "reserveInventoryFallback")
    @Retry(name = "inventoryService")
    public ReserveInventoryResponse reserveInventory(ReserveInventoryRequest request) {
        log.info("Calling Inventory SOAP service - reserveInventory for order: {}",
                request.getOrderId());

        ReserveInventoryResponse response = inventoryPort.reserveInventory(request);

        log.info("Inventory SOAP service responded: reservationId={}, allReserved={}",
                response.getReservationId(), response.isAllReserved());
        return response;
    }

    // Fallback methods
    private CheckInventoryResponse checkInventoryFallback(CheckInventoryRequest request, Throwable t) {
        log.error("Inventory service unavailable, fallback triggered for checkInventory: {}", t.getMessage());
        throw new RuntimeException("Inventory service is currently unavailable. Please try again later.", t);
    }

    private ReserveInventoryResponse reserveInventoryFallback(ReserveInventoryRequest request, Throwable t) {
        log.error("Inventory service unavailable, fallback triggered for reserveInventory: {}", t.getMessage());
        throw new RuntimeException("Inventory service is currently unavailable. Please try again later.", t);
    }
}
