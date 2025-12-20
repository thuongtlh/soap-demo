package com.demo.gateway.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger configuration for the Gateway REST API.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Order Gateway REST API",
                version = "1.0.0",
                description = """
                        REST API Gateway that orchestrates multiple backend SOAP services.

                        ## Architecture
                        This gateway demonstrates the Gateway pattern by:
                        - Aggregating data from Order SOAP service (port 8081) and Inventory SOAP service (port 8083)
                        - Applying resilience patterns (Circuit Breaker, Retry)
                        - Transforming requests/responses between REST and SOAP formats

                        ## Backend Services
                        - **Order Service**: http://localhost:8081/ws/orders.wsdl
                        - **Inventory Service**: http://localhost:8083/ws/inventory.wsdl

                        ## Key Components
                        - **Request Builders**: Transform REST DTOs to SOAP requests
                        - **Response Builders**: Aggregate SOAP responses to REST DTOs
                        - **Gateway**: Orchestrates calls to multiple services
                        """,
                contact = @Contact(name = "Demo Team", email = "demo@example.com"),
                license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")
        ),
        servers = {
                @Server(url = "http://localhost:8084", description = "Gateway server")
        }
)
public class OpenApiConfig {
}
