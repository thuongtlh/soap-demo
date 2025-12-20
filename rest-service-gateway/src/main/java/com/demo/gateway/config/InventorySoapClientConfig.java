package com.demo.gateway.config;

import com.demo.gateway.generated.inventory.InventoryPort;
import com.demo.gateway.generated.inventory.InventoryService;
import jakarta.xml.ws.BindingProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the Inventory SOAP client using JAX-WS.
 */
@Configuration
public class InventorySoapClientConfig {

    @Value("${soap.inventory-service.url}")
    private String inventoryServiceUrl;

    @Bean
    public InventoryService inventoryService() {
        return new InventoryService();
    }

    @Bean
    public InventoryPort inventoryPort(InventoryService inventoryService) {
        InventoryPort port = inventoryService.getInventoryPortSoap11();

        BindingProvider bindingProvider = (BindingProvider) port;
        bindingProvider.getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                inventoryServiceUrl
        );

        return port;
    }
}
