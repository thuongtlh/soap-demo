package com.demo.gateway.config;

import com.demo.gateway.generated.order.OrdersPort;
import com.demo.gateway.generated.order.OrdersService;
import jakarta.xml.ws.BindingProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the Order SOAP client using JAX-WS.
 */
@Configuration
public class OrderSoapClientConfig {

    @Value("${soap.order-service.url}")
    private String orderServiceUrl;

    @Bean
    public OrdersService ordersService() {
        return new OrdersService();
    }

    @Bean
    public OrdersPort ordersPort(OrdersService ordersService) {
        OrdersPort port = ordersService.getOrdersPortSoap11();

        BindingProvider bindingProvider = (BindingProvider) port;
        bindingProvider.getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                orderServiceUrl
        );

        return port;
    }
}
