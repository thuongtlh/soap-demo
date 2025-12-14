package com.demo.rest.config;

import com.demo.rest.generated.OrdersPort;
import com.demo.rest.generated.OrdersService;
import jakarta.xml.ws.BindingProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the SOAP client using JAX-WS (WSDL-first approach).
 *
 * This configuration creates a JAX-WS client from the generated stubs.
 * The stubs are generated from the WSDL file during build time by jaxws-maven-plugin.
 */
@Configuration
public class SoapClientConfig {

    @Value("${soap.service.url}")
    private String soapServiceUrl;

    /**
     * Create the JAX-WS service client.
     *
     * The OrdersService is generated from the WSDL by jaxws-maven-plugin.
     * It provides type-safe access to the SOAP operations.
     */
    @Bean
    public OrdersService ordersService() {
        return new OrdersService();
    }

    /**
     * Create the OrdersPort proxy for making SOAP calls.
     *
     * The port is configured with the runtime endpoint URL from properties.
     * This allows the endpoint to be different from the WSDL-defined location.
     */
    @Bean
    public OrdersPort ordersPort(OrdersService ordersService) {
        OrdersPort port = ordersService.getOrdersPortSoap11();

        // Override the endpoint URL from configuration
        BindingProvider bindingProvider = (BindingProvider) port;
        bindingProvider.getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                soapServiceUrl
        );

        return port;
    }
}
