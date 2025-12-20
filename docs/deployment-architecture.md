# Deployment Architecture

This diagram shows how the services are deployed and communicate at runtime.

```mermaid
graph TB
    subgraph "Development/Testing Environment"
        subgraph "Client Layer"
            Browser[Web Browser<br/>Swagger UI]
            Postman[Postman/curl<br/>API Testing]
        end
        
        subgraph "REST Service Gateway - localhost:8084"
            GatewayApp[Spring Boot Application<br/>GatewayServiceApplication]
            
            subgraph "Embedded Tomcat 8084"
                GatewayEndpoints[REST Endpoints<br/>/api/v1/orders]
                GatewaySwagger[Swagger UI<br/>/swagger-ui.html]
                GatewayOpenAPI[OpenAPI Spec<br/>/v3/api-docs]
            end
            
            subgraph "Gateway Beans"
                GatewayComponents[Controllers<br/>Services<br/>Gateway<br/>Mappers<br/>SOAP Clients]
            end
            
            subgraph "Resilience Components"
                CircuitBreaker[Circuit Breaker<br/>Resilience4j]
            end
            
            subgraph "JAX-WS Clients"
                OrderStubs[Order SOAP Stubs<br/>OrdersPort]
                InventoryStubs[Inventory SOAP Stubs<br/>InventoryPort]
            end
            
            GatewayApp --> GatewayEndpoints
            GatewayApp --> GatewaySwagger
            GatewayApp --> GatewayComponents
            GatewayComponents --> CircuitBreaker
            CircuitBreaker --> OrderStubs
            CircuitBreaker --> InventoryStubs
        end
        
        subgraph "REST Service - localhost:8082"
            RestApp[Spring Boot Application<br/>RestServiceApplication]
            
            subgraph "Embedded Tomcat 8082"
                RestEndpoints[REST Endpoints<br/>/api/v1/orders]
                SwaggerUI[Swagger UI<br/>/swagger-ui.html]
                OpenAPISpec[OpenAPI Spec<br/>/v3/api-docs]
            end
            
            subgraph "Application Beans"
                RestComponents[Controllers<br/>Services<br/>Mappers<br/>SOAP Client]
            end
            
            subgraph "JAX-WS Client"
                JAXWSStubs[Generated SOAP Stubs<br/>OrdersPort<br/>OrdersService]
            end
            
            RestApp --> RestEndpoints
            RestApp --> SwaggerUI
            RestApp --> OpenAPISpec
            RestApp --> RestComponents
            RestComponents --> JAXWSStubs
        end
        
        subgraph "Order SOAP Service - localhost:8081"
            SoapApp[Spring Boot Application<br/>SoapServiceApplication]
            
            subgraph "Embedded Tomcat 8081"
                SoapEndpoints[SOAP Endpoints<br/>/ws/*]
                WSDLEndpoint[WSDL Endpoint<br/>/ws/orders.wsdl]
            end
            
            subgraph "Application Beans "
                SoapComponents[Endpoints<br/>Services<br/>JAXB Types]
            end
            
            subgraph "Schema Resources"
                XSDSchema[XSD Schema<br/>order.xsd]
            end
            
            SoapApp --> SoapEndpoints
            SoapApp --> WSDLEndpoint
            SoapApp --> SoapComponents
            SoapApp --> XSDSchema
        end
        
        subgraph "Inventory SOAP Service - localhost:8083"
            InventoryApp[Spring Boot Application<br/>InventoryServiceApplication]
            
            subgraph "Embedded Tomcat 8083"
                InventoryEndpoints[SOAP Endpoints<br/>/ws/*]
                InventoryWSDL[WSDL Endpoint<br/>/ws/inventory.wsdl]
            end
            
            subgraph "Inventory Beans"
                InventoryComponents[Endpoints<br/>Services<br/>JAXB Types]
            end
            
            subgraph "Inventory Schema"
                InventoryXSD[XSD Schema<br/>inventory.xsd]
            end
            
            InventoryApp --> InventoryEndpoints
            InventoryApp --> InventoryWSDL
            InventoryApp --> InventoryComponents
            InventoryApp --> InventoryXSD
        end
        
        Browser -->|HTTP GET| GatewaySwagger
        Browser -->|HTTP GET| SwaggerUI
        Postman -->|HTTP POST/GET<br/>JSON| GatewayEndpoints
        Postman -->|HTTP POST/GET<br/>JSON| RestEndpoints
        
        JAXWSStubs -->|HTTP POST<br/>SOAP/XML| SoapEndpoints
        OrderStubs -->|HTTP POST<br/>SOAP/XML| SoapEndpoints
        InventoryStubs -->|HTTP POST<br/>SOAP/XML| InventoryEndpoints
        
        Postman -.can fetch.-> WSDLEndpoint
        Postman -.can fetch.-> InventoryWSDL
    end
    
    style Browser fill:#e1f5ff
    style Postman fill:#e1f5ff
    style GatewayApp fill:#e3f2fd
    style RestApp fill:#fff3e0
    style SoapApp fill:#f3e5f5
    style InventoryApp fill:#e1bee7
    style JAXWSStubs fill:#c5e1a5
    style OrderStubs fill:#c5e1a5
    style InventoryStubs fill:#c5e1a5
    style XSDSchema fill:#ffecb3
    style InventoryXSD fill:#ffecb3
    style CircuitBreaker fill:#ffcdd2
```

## Deployment Details

### REST Service Gateway (Port 8084)

#### Runtime Environment
- **Framework**: Spring Boot 3.4.6
- **Web Server**: Embedded Tomcat
- **Port**: 8084
- **Context Path**: /

#### Exposed Endpoints
- `POST /api/v1/orders` - Create order with inventory reservation
- `POST /api/v1/orders/simple` - Create order without inventory
- `GET /swagger-ui.html` - Interactive API documentation
- `GET /v3/api-docs` - OpenAPI specification (JSON)

#### Dependencies
- **Order SOAP Service**: Connects to `http://localhost:8081/ws`
- **Inventory SOAP Service**: Connects to `http://localhost:8083/ws`
- **Configuration**: `soap.order-service.url` and `soap.inventory-service.url` in application.yml

#### Key Components
- Spring MVC for REST endpoints
- JAX-WS for SOAP clients (Order + Inventory)
- MapStruct for object mapping
- Resilience4j for circuit breaker and retry
- SpringDoc OpenAPI for documentation
- Jakarta Validation for request validation

### REST Service (Port 8082)

#### Runtime Environment
- **Framework**: Spring Boot 3.4.6
- **Web Server**: Embedded Tomcat
- **Port**: 8082
- **Context Path**: /

#### Exposed Endpoints
- `POST /api/v1/orders` - Create new order
- `GET /api/v1/orders/{orderId}` - Retrieve order
- `GET /swagger-ui.html` - Interactive API documentation
- `GET /v3/api-docs` - OpenAPI specification (JSON)
- `GET /v3/api-docs.yaml` - OpenAPI specification (YAML)

#### Dependencies
- **SOAP Service**: Connects to `http://localhost:8081/ws`
- **Configuration**: `soap.service.url` property in application.yml

#### Key Components
- Spring MVC for REST endpoints
- JAX-WS for SOAP client
- MapStruct for object mapping
- SpringDoc OpenAPI for documentation
- Jakarta Validation for request validation

### Order SOAP Service (Port 8081)

#### Runtime Environment
- **Framework**: Spring Boot 3.4.6
- **Web Server**: Embedded Tomcat
- **Port**: 8081
- **Context Path**: /

#### Exposed Endpoints
- `POST /ws` - SOAP endpoint for order operations
- `GET /ws/orders.wsdl` - WSDL definition (auto-generated)

#### SOAP Operations
- `createOrder` - Create order operation
- `getOrder` - Retrieve order operation

#### Data Storage
- In-memory storage using `ConcurrentHashMap`
- For demonstration purposes only
- Data lost on restart

#### Key Components
- Spring Web Services for SOAP endpoints
- JAXB for XML marshalling/unmarshalling
- XSD schema for contract definition

### Inventory SOAP Service (Port 8083)

#### Runtime Environment
- **Framework**: Spring Boot 3.4.6
- **Web Server**: Embedded Tomcat
- **Port**: 8083
- **Context Path**: /

#### Exposed Endpoints
- `POST /ws` - SOAP endpoint for inventory operations
- `GET /ws/inventory.wsdl` - WSDL definition (auto-generated)

#### SOAP Operations
- `checkInventory` - Check inventory availability for products
- `reserveInventory` - Reserve inventory for an order

#### Data Storage
- In-memory storage using `HashMap`
- Pre-populated with sample inventory data
- For demonstration purposes only

#### Key Components
- Spring Web Services for SOAP endpoints
- JAXB for XML marshalling/unmarshalling
- XSD schema for contract definition

## Communication Flow

### Simple Flow (REST Service)

```mermaid
sequenceDiagram
    participant Client
    participant REST as REST Service<br/>:8082
    participant SOAP as Order SOAP<br/>:8081
    
    Client->>REST: HTTP Request<br/>(JSON)
    activate REST
    
    REST->>REST: Process & Map
    
    REST->>SOAP: HTTP POST /ws<br/>(SOAP/XML)
    activate SOAP
    
    SOAP->>SOAP: Process Request
    
    SOAP-->>REST: HTTP Response<br/>(SOAP/XML)
    deactivate SOAP
    
    REST->>REST: Map Response
    
    REST-->>Client: HTTP Response<br/>(JSON)
    deactivate REST
```

### Gateway Flow (REST Gateway with Multiple SOAP Services)

```mermaid
sequenceDiagram
    participant Client
    participant Gateway as REST Gateway<br/>:8084
    participant Order as Order SOAP<br/>:8081
    participant Inventory as Inventory SOAP<br/>:8083
    
    Client->>Gateway: HTTP POST /api/v1/orders<br/>(JSON)
    activate Gateway
    
    Gateway->>Gateway: Validate & Map
    
    Gateway->>Order: createOrder<br/>(SOAP/XML)
    activate Order
    Order-->>Gateway: CreateOrderResponse
    deactivate Order
    
    Gateway->>Inventory: reserveInventory<br/>(SOAP/XML)
    activate Inventory
    Inventory-->>Gateway: ReserveInventoryResponse
    deactivate Inventory
    
    Gateway->>Gateway: Aggregate Responses
    
    Gateway-->>Client: HTTP 201 Created<br/>(JSON with order + inventory)
    deactivate Gateway
```

## Network Configuration

### Local Development
```
┌─────────────────────────────────────────────────────────────┐
│                        localhost                             │
│                                                              │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐              │
│  │  :8084   │───▶│  :8081   │    │  :8083   │◀───┐         │
│  │ Gateway  │    │  Order   │    │Inventory │    │         │
│  └──────────┘    │  SOAP    │    │  SOAP    │    │         │
│       │          └──────────┘    └──────────┘    │         │
│       └──────────────────────────────────────────┘         │
│                                                              │
│  ┌──────────┐    ┌──────────┐                               │
│  │  :8082   │───▶│  :8081   │                               │
│  │  REST    │    │  Order   │                               │
│  └──────────┘    │  SOAP    │                               │
│       ▲          └──────────┘                               │
│       │                                                      │
│  ┌────┴────┐                                                │
│  │ Client  │                                                │
│  └─────────┘                                                │
└─────────────────────────────────────────────────────────────┘
```

### Port Allocation
- **8081**: Order SOAP Service
- **8082**: REST Service (simple)
- **8083**: Inventory SOAP Service
- **8084**: REST Service Gateway (orchestrates multiple SOAP services)
- All services run on localhost in development

## Build and Runtime Process

```mermaid
graph LR
    subgraph "Build Time"
        OrderXSD[XSD Schema<br/>order.xsd]
        InventoryXSD[XSD Schema<br/>inventory.xsd]
        OrderWSDL[WSDL File<br/>orders.wsdl]
        InventoryWSDL[WSDL File<br/>inventory.wsdl]
        
        OrderXSD -->|Spring WS generates| OrderWSDL
        InventoryXSD -->|Spring WS generates| InventoryWSDL
        OrderWSDL -->|jaxws-maven-plugin| OrderStubs[Order JAX-WS Stubs]
        InventoryWSDL -->|jaxws-maven-plugin| InventoryStubs[Inventory JAX-WS Stubs]
        
        MapStructIF[MapStruct<br/>Interfaces]
        MapStructIF -->|mapstruct-processor| MapStructImpl[Generated<br/>Mapper Implementations]
    end
    
    subgraph "Runtime"
        OrderStubs --> GatewayRuntime[Gateway Service<br/>Runtime]
        InventoryStubs --> GatewayRuntime
        MapStructImpl --> GatewayRuntime
        
        OrderStubs --> RestRuntime[REST Service<br/>Runtime]
        MapStructImpl --> RestRuntime
        
        OrderXSD --> OrderSoapRuntime[Order SOAP<br/>Runtime]
        InventoryXSD --> InventorySoapRuntime[Inventory SOAP<br/>Runtime]
    end
    
    style OrderXSD fill:#fff9c4
    style InventoryXSD fill:#fff9c4
    style OrderWSDL fill:#fff9c4
    style InventoryWSDL fill:#fff9c4
    style OrderStubs fill:#c5e1a5
    style InventoryStubs fill:#c5e1a5
    style MapStructImpl fill:#c5e1a5
    style GatewayRuntime fill:#e3f2fd
    style RestRuntime fill:#bbdefb
    style OrderSoapRuntime fill:#f8bbd0
    style InventorySoapRuntime fill:#e1bee7
```

### Build Steps

1. **Order SOAP Service Build**
   - XSD schema (`order.xsd`) loaded as resource
   - Spring WS generates WSDL at startup
   - JAXB generates classes from XSD (internal)

2. **Inventory SOAP Service Build**
   - XSD schema (`inventory.xsd`) loaded as resource
   - Spring WS generates WSDL at startup
   - JAXB generates classes from XSD (internal)
   
3. **REST Service Build**
   - Copy WSDL from Order SOAP service (manual step when schema changes)
   - `jaxws-maven-plugin` generates JAX-WS client stubs from WSDL
   - `mapstruct-processor` generates mapper implementations
   - Compile application code with generated classes

4. **REST Gateway Service Build**
   - Copy WSDLs from both SOAP services (Order + Inventory)
   - `jaxws-maven-plugin` generates JAX-WS client stubs from both WSDLs
   - `mapstruct-processor` generates mapper implementations
   - Compile application code with generated classes
    
5. **Packaging**
   - Each service packaged as standalone JAR
   - Includes all dependencies (fat JAR)
   - Can run with `java -jar`

## Starting the Services

### Order of Startup
1. **Start Order SOAP Service First** (Port 8081)
   ```bash
   cd soap-service
   mvn spring-boot:run
   ```

2. **Start Inventory SOAP Service** (Port 8083)
   ```bash
   cd inventory-soap-service
   mvn spring-boot:run
   ```

3. **Start REST Service** (Port 8082) - Optional
   ```bash
   cd rest-service
   mvn spring-boot:run
   ```

4. **Start REST Gateway Service** (Port 8084)
   ```bash
   cd rest-service-gateway
   mvn spring-boot:run
   ```

### Health Check
- Order SOAP Service: `curl http://localhost:8081/ws/orders.wsdl`
- Inventory SOAP Service: `curl http://localhost:8083/ws/inventory.wsdl`
- REST Service: `curl http://localhost:8082/v3/api-docs`
- REST Gateway Service: `curl http://localhost:8084/v3/api-docs`

## Production Considerations

### Potential Production Setup

```mermaid
graph TB
    subgraph "Load Balancer"
        LB[Load Balancer]
    end
    
    subgraph "REST Gateway Cluster"
        GW1[Gateway Instance 1]
        GW2[Gateway Instance 2]
        GW3[Gateway Instance 3]
    end
    
    subgraph "Order SOAP Cluster"
        OrderSOAP1[Order SOAP 1]
        OrderSOAP2[Order SOAP 2]
    end
    
    subgraph "Inventory SOAP Cluster"
        InvSOAP1[Inventory SOAP 1]
        InvSOAP2[Inventory SOAP 2]
    end
    
    subgraph "Data Layer"
        DB[(Database<br/>PostgreSQL/MySQL)]
        Cache[(Cache<br/>Redis)]
    end
    
    LB --> GW1
    LB --> GW2
    LB --> GW3
    
    GW1 --> OrderSOAP1
    GW1 --> OrderSOAP2
    GW1 --> InvSOAP1
    GW1 --> InvSOAP2
    GW2 --> OrderSOAP1
    GW2 --> OrderSOAP2
    GW2 --> InvSOAP1
    GW2 --> InvSOAP2
    GW3 --> OrderSOAP1
    GW3 --> OrderSOAP2
    GW3 --> InvSOAP1
    GW3 --> InvSOAP2
    
    OrderSOAP1 --> DB
    OrderSOAP2 --> DB
    InvSOAP1 --> DB
    InvSOAP2 --> DB
    OrderSOAP1 --> Cache
    OrderSOAP2 --> Cache
```

### Production Enhancements Needed
1. **Replace In-Memory Storage**: Use PostgreSQL/MySQL database
2. **Add Caching**: Redis for frequently accessed data
3. **Service Discovery**: Use Consul/Eureka for dynamic service locations
4. **Load Balancing**: Distribute traffic across multiple instances
5. **Monitoring**: Add metrics, logging, tracing (Prometheus, ELK, Zipkin)
6. **Security**: Add authentication, authorization, TLS/SSL
7. **Configuration**: Externalize configuration (Spring Cloud Config)
8. **Resilience**: Circuit breakers and retry (already in Gateway with Resilience4j)
9. **Container Deployment**: Docker containers, Kubernetes orchestration
10. **API Gateway**: Already implemented with REST Gateway pattern

## Configuration Files

### REST Gateway Service - application.yml
```yaml
server:
  port: 8084

soap:
  order-service:
    url: http://localhost:8081/ws
  inventory-service:
    url: http://localhost:8083/ws

resilience4j:
  circuitbreaker:
    instances:
      orderService:
        slidingWindowSize: 10
        failureRateThreshold: 50
      inventoryService:
        slidingWindowSize: 10
        failureRateThreshold: 50
  retry:
    instances:
      orderService:
        maxAttempts: 3
        waitDuration: 1s
      inventoryService:
        maxAttempts: 3
        waitDuration: 1s

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
```

### REST Service - application.yml
```yaml
server:
  port: 8082

soap:
  service:
    url: http://localhost:8081/ws

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
```

### Order SOAP Service - application.yml
```yaml
server:
  port: 8081

spring:
  ws:
    path: /ws
```

### Inventory SOAP Service - application.yml
```yaml
server:
  port: 8083

spring:
  ws:
    path: /ws
```

## Monitoring Endpoints

### Actuator Endpoints (If Enabled)
- `/actuator/health` - Health check
- `/actuator/metrics` - Metrics
- `/actuator/info` - Application info
- `/actuator/env` - Environment properties

### Custom Monitoring
- Track request/response times
- Monitor SOAP call success/failure rates
- Log mapping exceptions
- Track API usage patterns
- Monitor circuit breaker states (Gateway)
