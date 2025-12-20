# Component Diagram

This diagram shows all the components in the system and their dependencies.

## REST Service Gateway Components

```mermaid
graph TB
    subgraph "REST Service Gateway Components"
        subgraph "Controller Layer"
            GatewayOrderController[OrderController]
        end
        
        subgraph "Service Layer"
            GatewayOrderService[OrderService]
        end
        
        subgraph "Gateway Layer"
            OrderGateway[OrderGateway<br/>Service Orchestration]
        end
        
        subgraph "Client Layer"
            OrderSoapClient[OrderSoapClient]
            InventorySoapClient[InventorySoapClient]
        end
        
        subgraph "Resilience Layer"
            CircuitBreaker[Resilience4j<br/>Circuit Breaker]
            RetryMechanism[Resilience4j<br/>Retry]
        end
        
        subgraph "Request/Response Builders"
            OrderGatewayRequest[OrderGatewayRequest]
            OrderGatewayResponse[OrderGatewayResponse]
            OrderSoapRequestBuilder[OrderSoapRequestBuilder]
            InventorySoapRequestBuilder[InventorySoapRequestBuilder]
        end
        
        subgraph "DTO Layer"
            GatewayCreateOrderReqDto[CreateOrderRequestDto]
            GatewayCreateOrderResDto[CreateOrderResponseDto]
            GatewayCustomerDto[CustomerDto]
            GatewayOrderItemDto[OrderItemDto]
        end
        
        subgraph "Generated SOAP Stubs"
            OrdersPortGW[OrdersPort<br/>Order Service]
            InventoryPort[InventoryPort<br/>Inventory Service]
        end
        
        subgraph "Configuration"
            GatewayConfig[SoapClientConfig]
            GatewayOpenApiConfig[OpenApiConfig]
        end
    end
    
    %% Gateway Dependencies
    GatewayOrderController --> GatewayOrderService
    GatewayOrderService --> OrderGateway
    OrderGateway --> OrderSoapClient
    OrderGateway --> InventorySoapClient
    OrderGateway --> OrderGatewayRequest
    OrderGateway --> OrderGatewayResponse
    OrderSoapClient --> CircuitBreaker
    InventorySoapClient --> CircuitBreaker
    CircuitBreaker --> RetryMechanism
    OrderSoapClient --> OrdersPortGW
    InventorySoapClient --> InventoryPort
    
    style GatewayOrderController fill:#e3f2fd
    style OrderGateway fill:#bbdefb
    style OrderSoapClient fill:#90caf9
    style InventorySoapClient fill:#90caf9
    style CircuitBreaker fill:#ffcdd2
    style RetryMechanism fill:#ffcdd2
```

## REST Service Components (Simple)

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
    
    style OrderController fill:#bbdefb
    style OrderService fill:#c5cae9
    style SoapOrderClient fill:#d1c4e9
    style SoapTypes fill:#dcedc8
```

## Order SOAP Service Components

```mermaid
graph TB
    subgraph "Order SOAP Service Components"
        subgraph "Endpoint Layer"
            OrderEndpoint[OrderEndpoint]
        end
        
        subgraph "Service Layer"
            OrderProcessingService[OrderProcessingService]
        end
        
        subgraph "Configuration"
            WebServiceConfig[WebServiceConfig]
        end
        
        subgraph "Generated Types"
            JaxbTypes[Generated JAXB Types<br/>from XSD]
        end
        
        subgraph "Schema Definition"
            OrderXSD[order.xsd<br/>Schema Definition]
        end
        
        subgraph "Storage"
            OrderStorage[(In-Memory Storage)]
        end
    end
    
    %% SOAP Service Dependencies
    OrderEndpoint --> OrderProcessingService
    OrderEndpoint --> JaxbTypes
    
    OrderProcessingService --> JaxbTypes
    OrderProcessingService --> OrderStorage
    
    WebServiceConfig --> OrderXSD
    WebServiceConfig -.generates.-> JaxbTypes
    
    style OrderEndpoint fill:#f8bbd0
    style OrderProcessingService fill:#ffccbc
    style OrderXSD fill:#fff9c4
    style JaxbTypes fill:#dcedc8
    style OrderStorage fill:#e8f5e9
```

## Inventory SOAP Service Components

```mermaid
graph TB
    subgraph "Inventory SOAP Service Components"
        subgraph "Endpoint Layer"
            InventoryEndpoint[InventoryEndpoint]
        end
        
        subgraph "Service Layer"
            InventoryService[InventoryService]
        end
        
        subgraph "Configuration"
            InventoryWebServiceConfig[WebServiceConfig]
        end
        
        subgraph "Generated Types"
            InventoryJaxbTypes[Generated JAXB Types<br/>from XSD]
        end
        
        subgraph "Schema Definition"
            InventoryXSD[inventory.xsd<br/>Schema Definition]
        end
        
        subgraph "Storage"
            InventoryStorage[(In-Memory Storage)]
        end
    end
    
    %% Inventory Service Dependencies
    InventoryEndpoint --> InventoryService
    InventoryEndpoint --> InventoryJaxbTypes
    
    InventoryService --> InventoryJaxbTypes
    InventoryService --> InventoryStorage
    
    InventoryWebServiceConfig --> InventoryXSD
    InventoryWebServiceConfig -.generates.-> InventoryJaxbTypes
    
    style InventoryEndpoint fill:#e1bee7
    style InventoryService fill:#ce93d8
    style InventoryXSD fill:#fff9c4
    style InventoryJaxbTypes fill:#dcedc8
    style InventoryStorage fill:#c8e6c9
```

## Component Relationships

### REST Service Gateway Components

#### Controller Layer
- **OrderController**: Entry point for REST API requests with inventory orchestration

#### Service Layer
- **OrderService**: Delegates to OrderGateway for multi-service orchestration

#### Gateway Layer
- **OrderGateway**: Orchestrates calls to Order SOAP and Inventory SOAP services

#### Client Layer
- **OrderSoapClient**: Communicates with Order SOAP service via JAX-WS
- **InventorySoapClient**: Communicates with Inventory SOAP service via JAX-WS

#### Resilience Layer
- **Resilience4j Circuit Breaker**: Protects against cascading failures
- **Resilience4j Retry**: Implements retry logic with exponential backoff

### REST Service Components (Simple)

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

### Order SOAP Service Components

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

### Inventory SOAP Service Components

#### Endpoint Layer
- **InventoryEndpoint**: Entry point for inventory SOAP requests

#### Service Layer
- **InventoryService**: Manages inventory checking and reservation

#### Operations
- **checkInventory**: Check product availability
- **reserveInventory**: Reserve inventory for an order

#### Configuration
- **WebServiceConfig**: Configures Spring WS and WSDL generation

#### Schema Definition
- **inventory.xsd**: XSD schema defining inventory service contract

## Key Design Patterns

1. **Separation of Concerns**: Clear layer separation (Controller, Service, Client)
2. **Dependency Injection**: Spring-managed beans with constructor injection
3. **Contract-First**: XSD defines the contract, code is generated
4. **Mapper Pattern**: MapStruct handles object conversion
5. **Facade Pattern**: Service layer provides a simplified interface
6. **Proxy Pattern**: JAX-WS generated proxies handle SOAP communication
7. **Gateway Pattern**: Orchestrates calls to multiple backend services
8. **Circuit Breaker Pattern**: Resilience4j protects against cascading failures
