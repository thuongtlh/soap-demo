package com.demo.gateway.gateway.request;

import com.demo.gateway.dto.OrderItemDto;
import com.demo.gateway.generated.inventory.CheckInventoryRequest;
import com.demo.gateway.generated.inventory.ReservationItemType;
import com.demo.gateway.generated.inventory.ReserveInventoryRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Builder for creating SOAP Inventory Service requests.
 * Handles transformation from gateway model to SOAP generated classes.
 */
@Slf4j
@Component
public class InventorySoapRequestBuilder {

    /**
     * Build SOAP CheckInventoryRequest from product IDs.
     */
    public CheckInventoryRequest buildCheckInventoryRequest(List<String> productIds) {
        log.debug("Building SOAP CheckInventoryRequest for {} products", productIds.size());

        CheckInventoryRequest request = new CheckInventoryRequest();
        request.getProductIds().addAll(productIds);
        return request;
    }

    /**
     * Build SOAP ReserveInventoryRequest from order details.
     */
    public ReserveInventoryRequest buildReserveInventoryRequest(String orderId, List<OrderItemDto> items) {
        log.debug("Building SOAP ReserveInventoryRequest for order: {}", orderId);

        ReserveInventoryRequest request = new ReserveInventoryRequest();
        request.setOrderId(orderId);

        items.forEach(item -> {
            ReservationItemType reservationItem = new ReservationItemType();
            reservationItem.setProductId(item.getProductId());
            reservationItem.setRequestedQuantity(item.getQuantity());
            request.getItems().add(reservationItem);
        });

        return request;
    }
}
