# System Architecture

This diagram shows the high-level architecture of the REST to SOAP integration demo application.

```mermaid
graph TB
    subgraph "External"
        Client[REST Client<br/>Postman/curl/Browser]
    end
    
    subgraph "REST Service Gateway - Port 8084"
        GatewayController[OrderController<br/>REST Endpoints]
        GatewayService[OrderService<br/>Business Logic]
        OrderGateway[OrderGateway<br/>Service Orchestration]
        GatewayMappers[MapStruct Mappers<br/>DTO Conversion]
        OrderClient[OrderSoapClient<br/>JAX-WS Client]
        InventoryClient[InventorySoapClient<br/>JAX-WS Client]
        Resilience[Resilience4j<br/>Circuit Breaker]
        GatewayOpenAPI[OpenAPI/Swagger<br/>API Documentation]
        
        GatewayController --> GatewayService
        GatewayService --> OrderGateway
        OrderGateway --> GatewayMappers
        OrderGateway --> OrderClient
        OrderGateway --> InventoryClient
        OrderClient --> Resilience
        InventoryClient --> Resilience
        GatewayController -.documents.-> GatewayOpenAPI
    end
    
    subgraph "REST Service - Port 8082"
        RestController[OrderController<br/>REST Endpoints]
        RestService[OrderService<br/>Business Logic]
        Mappers[MapStruct Mappers<br/>DTO Conversion]
        SoapClient[SoapOrderClient<br/>JAX-WS Client]
        OpenAPI[OpenAPI/Swagger<br/>API Documentation]
        
        RestController --> RestService
        RestService --> Mappers
        RestService --> SoapClient
        Mappers --> SoapClient
        RestController -.documents.-> OpenAPI
    end
    
    subgraph "Order SOAP Service - Port 8081"
        SoapEndpoint[OrderEndpoint<br/>SOAP Endpoint]
        SoapService[OrderProcessingService<br/>Business Logic]
        WSDL[WSDL/XSD<br/>Contract Definition]
        Storage[(In-Memory Storage<br/>Order Data)]
        
        SoapEndpoint --> SoapService
        SoapService --> Storage
        WSDL -.defines.-> SoapEndpoint
    end
    
    subgraph "Inventory SOAP Service - Port 8083"
        InventoryEndpoint[InventoryEndpoint<br/>SOAP Endpoint]
        InventoryService[InventoryService<br/>Business Logic]
        InventoryWSDL[WSDL/XSD<br/>Contract Definition]
        InventoryStorage[(In-Memory Storage<br/>Inventory Data)]
        
        InventoryEndpoint --> InventoryService
        InventoryService --> InventoryStorage
        InventoryWSDL -.defines.-> InventoryEndpoint
    end
    
    Client -->|HTTP POST/GET<br/>JSON| GatewayController
    Client -->|HTTP POST/GET<br/>JSON| RestController
    SoapClient -->|SOAP/XML<br/>HTTP| SoapEndpoint
    OrderClient -->|SOAP/XML<br/>HTTP| SoapEndpoint
    InventoryClient -->|SOAP/XML<br/>HTTP| InventoryEndpoint
    Client -.browses.-> GatewayOpenAPI
    Client -.browses.-> OpenAPI
    
    style Client fill:#e1f5ff
    style GatewayService fill:#e3f2fd
    style OrderGateway fill:#bbdefb
    style RestService fill:#fff4e6
    style SoapService fill:#f3e5f5
    style InventoryService fill:#e1bee7
    style Storage fill:#e8f5e9
    style InventoryStorage fill:#c8e6c9
    style OpenAPI fill:#fce4ec
    style GatewayOpenAPI fill:#fce4ec
    style WSDL fill:#fce4ec
    style InventoryWSDL fill:#fce4ec
    style Resilience fill:#ffecb3
```

## Key Components

### REST Service Gateway Layer (Port 8084)
- **OrderController**: Exposes REST API endpoints for order operations with inventory
- **OrderService**: Contains business logic and orchestrates the flow
- **OrderGateway**: Orchestrates calls to multiple SOAP backend services
- **MapStruct Mappers**: Converts between REST DTOs and SOAP types
- **OrderSoapClient**: JAX-WS client for communicating with Order SOAP service
- **InventorySoapClient**: JAX-WS client for communicating with Inventory SOAP service
- **Resilience4j**: Circuit breaker and retry patterns for resilience
- **OpenAPI**: Provides interactive API documentation via Swagger UI

### REST Service Layer (Port 8082)
- **OrderController**: Exposes REST API endpoints for order operations
- **OrderService**: Contains business logic and orchestrates the flow
- **MapStruct Mappers**: Converts between REST DTOs and SOAP types
- **SoapOrderClient**: JAX-WS client for communicating with SOAP service
- **OpenAPI**: Provides interactive API documentation via Swagger UI

### Order SOAP Service Layer (Port 8081)
- **OrderEndpoint**: Handles incoming SOAP requests for orders
- **OrderProcessingService**: Processes orders and manages data
- **WSDL/XSD**: Contract-first definition of the service
- **In-Memory Storage**: Simulates data persistence for demo purposes

### Inventory SOAP Service Layer (Port 8083)
- **InventoryEndpoint**: Handles incoming SOAP requests for inventory
- **InventoryService**: Manages inventory checking and reservation
- **WSDL/XSD**: Contract-first definition of the inventory service
- **In-Memory Storage**: Simulates inventory data persistence

## Communication Flow

### Simple Flow (REST Service → Order SOAP Service)
1. **REST Client** sends HTTP requests with JSON payloads to REST Service (8082)
2. **REST Service** validates and processes the requests
3. **MapStruct** converts REST DTOs to SOAP objects
4. **SOAP Client** sends SOAP/XML requests to Order SOAP Service
5. **SOAP Service** processes requests and returns responses
6. **MapStruct** converts SOAP responses back to REST DTOs
7. **REST Service** returns JSON responses to client

### Gateway Flow (REST Gateway → Multiple SOAP Services)
1. **REST Client** sends HTTP requests with JSON payloads to REST Gateway (8084)
2. **Gateway Controller** delegates to OrderService
3. **OrderGateway** orchestrates calls to multiple backend services:
   - Creates order via Order SOAP Service (8081)
   - Reserves inventory via Inventory SOAP Service (8083)
4. **Resilience4j** provides circuit breaker and retry for SOAP calls
5. **Gateway** aggregates responses from multiple services
6. **REST Gateway** returns unified JSON response to client
