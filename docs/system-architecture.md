# System Architecture

This diagram shows the high-level architecture of the REST to SOAP integration demo application.

```mermaid
graph TB
    subgraph "External"
        Client[REST Client<br/>Postman/curl/Browser]
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
    
    subgraph "SOAP Service - Port 8081"
        SoapEndpoint[OrderEndpoint<br/>SOAP Endpoint]
        SoapService[OrderProcessingService<br/>Business Logic]
        WSDL[WSDL/XSD<br/>Contract Definition]
        Storage[(In-Memory Storage<br/>Order Data)]
        
        SoapEndpoint --> SoapService
        SoapService --> Storage
        WSDL -.defines.-> SoapEndpoint
    end
    
    Client -->|HTTP POST/GET<br/>JSON| RestController
    SoapClient -->|SOAP/XML<br/>HTTP| SoapEndpoint
    Client -.browses.-> OpenAPI
    
    style Client fill:#e1f5ff
    style RestService fill:#fff4e6
    style SoapService fill:#f3e5f5
    style Storage fill:#e8f5e9
    style OpenAPI fill:#fce4ec
    style WSDL fill:#fce4ec
```

## Key Components

### REST Service Layer
- **OrderController**: Exposes REST API endpoints for order operations
- **OrderService**: Contains business logic and orchestrates the flow
- **MapStruct Mappers**: Converts between REST DTOs and SOAP types
- **SoapOrderClient**: JAX-WS client for communicating with SOAP service
- **OpenAPI**: Provides interactive API documentation via Swagger UI

### SOAP Service Layer
- **OrderEndpoint**: Handles incoming SOAP requests
- **OrderProcessingService**: Processes orders and manages data
- **WSDL/XSD**: Contract-first definition of the service
- **In-Memory Storage**: Simulates data persistence for demo purposes

## Communication Flow

1. **REST Client** sends HTTP requests with JSON payloads
2. **REST Service** validates and processes the requests
3. **MapStruct** converts REST DTOs to SOAP objects
4. **SOAP Client** sends SOAP/XML requests to SOAP Service
5. **SOAP Service** processes requests and returns responses
6. **MapStruct** converts SOAP responses back to REST DTOs
7. **REST Service** returns JSON responses to client
