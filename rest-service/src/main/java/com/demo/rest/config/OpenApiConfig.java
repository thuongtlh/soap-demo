package com.demo.rest.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI Configuration for REST API documentation.
 *
 * This configuration:
 * 1. Defines API metadata (title, version, description)
 * 2. Documents the relationship between REST and SOAP
 * 3. Provides Swagger UI for API exploration
 *
 * OpenAPI is relevant here because:
 * - It provides a standard way to document REST APIs
 * - Swagger UI allows developers to test the API interactively
 * - It generates API documentation automatically from annotations
 * - It helps with API versioning and consumer integration
 *
 * Access Swagger UI at: http://localhost:8082/swagger-ui.html
 * Access OpenAPI spec at: http://localhost:8082/v3/api-docs
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Order Management REST API",
                version = "1.0.0",
                description = """
                        REST API for Order Management that integrates with a SOAP backend service.

                        ## Architecture
                        This API acts as a REST facade over a SOAP backend:

                        ```
                        REST Client -> REST API -> MapStruct -> SOAP Client -> SOAP Service
                        ```

                        ## Features
                        - **REST to SOAP Integration**: Accepts REST requests and converts them to SOAP
                        - **MapStruct Mapping**: Uses MapStruct for efficient object mapping between DTOs
                        - **Validation**: Jakarta Bean Validation on incoming requests
                        - **Error Handling**: Consistent error responses for all error scenarios

                        ## Flow Example
                        1. Client sends POST /api/v1/orders with JSON body
                        2. REST service validates the request
                        3. MapStruct converts REST DTO to SOAP request type
                        4. SOAP client sends request to SOAP backend (port 8081)
                        5. SOAP response is received
                        6. MapStruct converts SOAP response to REST DTO
                        7. REST response returned to client as JSON
                        """,
                contact = @Contact(
                        name = "Demo Team",
                        email = "demo@example.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8082", description = "Local development server")
        }
)
public class OpenApiConfig {

    /**
     * Customize OpenAPI documentation.
     * This customizer adds additional details to the generated documentation.
     */
    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            // Add global description about the SOAP integration
            if (openApi.getInfo() != null) {
                String existingDescription = openApi.getInfo().getDescription();
                openApi.getInfo().setDescription(existingDescription +
                        "\n\n## SOAP Backend\n" +
                        "- WSDL: http://localhost:8081/ws/orders.wsdl\n" +
                        "- Endpoint: http://localhost:8081/ws\n");
            }
        };
    }
}
