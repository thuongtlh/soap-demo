# Component Diagram

This diagram shows all the components in the system and their dependencies.

```mermaid
graph TB
    subgraph "REST Service Components"
        subgraph "Controller Layer"
            OrderController[OrderController]
        end
        
        subgraph "Service Layer"
            OrderService[OrderService]
        end
        
        subgraph "Client Layer"
            SoapOrderClient[SoapOrderClient]
        end
        
        subgraph "Mapper Layer"
            OrderMapper[OrderMapper]
            CustomerMapper[CustomerMapper]
            OrderItemMapper[OrderItemMapper]
            AddressMapper[AddressMapper]
        end
        
        subgraph "DTO Layer"
            CreateOrderReqDto[CreateOrderRequestDto]
            CreateOrderResDto[CreateOrderResponseDto]
            GetOrderResDto[GetOrderResponseDto]
            CustomerDto[CustomerDto]
            OrderItemDto[OrderItemDto]
            AddressDto[AddressDto]
        end
        
        subgraph "Configuration"
            SoapClientConfig[SoapClientConfig]
            OpenApiConfig[OpenApiConfig]
        end
        
        subgraph "Generated SOAP Stubs"
            OrdersPort[OrdersPort<br/>JAX-WS Interface]
            OrdersService[OrdersService<br/>JAX-WS Service]
            SoapTypes[CreateOrderRequest<br/>CreateOrderResponse<br/>GetOrderRequest<br/>GetOrderResponse<br/>JAXB Types]
        end
        
        subgraph "Exception Handling"
            GlobalExceptionHandler[GlobalExceptionHandler]
        end
    end
    
    subgraph "SOAP Service Components"
        subgraph "Endpoint Layer"
            OrderEndpoint[OrderEndpoint]
        end
        
        subgraph "Service Layer "
            OrderProcessingService[OrderProcessingService]
        end
        
        subgraph "Configuration "
            WebServiceConfig[WebServiceConfig]
        end
        
        subgraph "Generated Types"
            JaxbTypes[Generated JAXB Types<br/>from XSD]
        end
        
        subgraph "Schema Definition"
            OrderXSD[order.xsd<br/>Schema Definition]
        end
    end
    
    %% REST Service Dependencies
    OrderController --> OrderService
    OrderController --> CreateOrderReqDto
    OrderController --> CreateOrderResDto
    OrderController --> GetOrderResDto
    OrderController --> GlobalExceptionHandler
    
    OrderService --> SoapOrderClient
    OrderService --> OrderMapper
    
    OrderMapper --> CustomerMapper
    OrderMapper --> OrderItemMapper
    OrderMapper --> AddressMapper
    OrderMapper --> CreateOrderReqDto
    OrderMapper --> CreateOrderResDto
    OrderMapper --> GetOrderResDto
    OrderMapper --> SoapTypes
    
    CustomerMapper --> CustomerDto
    CustomerMapper --> AddressMapper
    OrderItemMapper --> OrderItemDto
    AddressMapper --> AddressDto
    
    SoapOrderClient --> OrdersPort
    SoapOrderClient --> SoapTypes
    
    SoapClientConfig --> OrdersService
    SoapClientConfig --> OrdersPort
    
    OrdersService --> OrdersPort
    
    %% SOAP Service Dependencies
    OrderEndpoint --> OrderProcessingService
    OrderEndpoint --> JaxbTypes
    
    OrderProcessingService --> JaxbTypes
    
    WebServiceConfig --> OrderXSD
    WebServiceConfig -.generates.-> JaxbTypes
    
    OrderXSD -.generates WSDL.-> OrdersService
    
    %% Cross-service dependency
    OrdersPort -->|SOAP/HTTP| OrderEndpoint
    
    style OrderController fill:#bbdefb
    style OrderService fill:#c5cae9
    style SoapOrderClient fill:#d1c4e9
    style OrderEndpoint fill:#f8bbd0
    style OrderProcessingService fill:#ffccbc
    style OrderXSD fill:#fff9c4
    style SoapTypes fill:#dcedc8
    style JaxbTypes fill:#dcedc8
```

## Component Relationships

### REST Service Components

#### Controller Layer
- **OrderController**: Entry point for REST API requests, handles HTTP concerns

#### Service Layer
- **OrderService**: Orchestrates business logic and coordinates mappers and SOAP client

#### Client Layer
- **SoapOrderClient**: Encapsulates SOAP communication using JAX-WS

#### Mapper Layer
- **OrderMapper**: Main mapper coordinating order conversion
- **CustomerMapper**: Converts customer information
- **OrderItemMapper**: Converts order item details
- **AddressMapper**: Converts address information

#### DTO Layer
- Contains REST-specific Data Transfer Objects for clean API contracts

#### Configuration
- **SoapClientConfig**: Configures JAX-WS client beans
- **OpenApiConfig**: Configures Swagger/OpenAPI documentation

#### Generated SOAP Stubs
- **OrdersPort**: Type-safe interface for SOAP operations
- **OrdersService**: JAX-WS service client
- **JAXB Types**: Generated from WSDL for type-safe SOAP communication

### SOAP Service Components

#### Endpoint Layer
- **OrderEndpoint**: Entry point for SOAP requests using @PayloadRoot

#### Service Layer
- **OrderProcessingService**: Business logic and data management

#### Configuration
- **WebServiceConfig**: Configures Spring WS and WSDL generation

#### Schema Definition
- **order.xsd**: XSD schema defining the service contract (source of truth)

#### Generated Types
- **JAXB Types**: Generated from XSD for marshalling/unmarshalling XML

## Key Design Patterns

1. **Separation of Concerns**: Clear layer separation (Controller, Service, Client)
2. **Dependency Injection**: Spring-managed beans with constructor injection
3. **Contract-First**: XSD defines the contract, code is generated
4. **Mapper Pattern**: MapStruct handles object conversion
5. **Facade Pattern**: Service layer provides a simplified interface
6. **Proxy Pattern**: JAX-WS generated proxies handle SOAP communication
