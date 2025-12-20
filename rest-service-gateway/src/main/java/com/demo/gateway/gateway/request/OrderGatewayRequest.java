package com.demo.gateway.gateway.request;

import com.demo.gateway.dto.CustomerDto;
import com.demo.gateway.dto.OrderItemDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Gateway-level request object for order creation.
 * This is the internal representation used by the Gateway layer.
 * It may be enriched with additional data from other services.
 */
@Data
@Builder
public class OrderGatewayRequest {
    private CustomerDto customer;
    private List<OrderItemDto> items;
    private String notes;
    private boolean priority;

    // Enriched data (populated by gateway)
    private boolean inventoryChecked;
    private boolean inventoryAvailable;
}
