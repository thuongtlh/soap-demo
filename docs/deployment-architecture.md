# Deployment Architecture

This diagram shows how the services are deployed and communicate at runtime.

```mermaid
graph TB
    subgraph "Development/Testing Environment"
        subgraph "Client Layer"
            Browser[Web Browser<br/>Swagger UI]
            Postman[Postman/curl<br/>API Testing]
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
        
        subgraph "SOAP Service - localhost:8081"
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
        
        Browser -->|HTTP GET| SwaggerUI
        Browser -->|HTTP GET| OpenAPISpec
        Postman -->|HTTP POST/GET<br/>JSON| RestEndpoints
        Browser -->|Try It Out| RestEndpoints
        
        JAXWSStubs -->|HTTP POST<br/>SOAP/XML| SoapEndpoints
        
        Postman -.can fetch.-> WSDLEndpoint
    end
    
    style Browser fill:#e1f5ff
    style Postman fill:#e1f5ff
    style RestApp fill:#fff3e0
    style SoapApp fill:#f3e5f5
    style JAXWSStubs fill:#c5e1a5
    style XSDSchema fill:#ffecb3
```

## Deployment Details

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

### SOAP Service (Port 8081)

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

## Communication Flow

```mermaid
sequenceDiagram
    participant Client
    participant REST as REST Service<br/>:8082
    participant SOAP as SOAP Service<br/>:8081
    
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

## Network Configuration

### Local Development
```
┌─────────────────────────────────────┐
│          localhost                  │
│                                     │
│  ┌──────────┐      ┌──────────┐   │
│  │  :8082   │─────▶│  :8081   │   │
│  │  REST    │      │  SOAP    │   │
│  └──────────┘      └──────────┘   │
│       ▲                             │
│       │                             │
│  ┌────┴────┐                       │
│  │ Client  │                       │
│  └─────────┘                       │
└─────────────────────────────────────┘
```

### Port Allocation
- **8081**: SOAP Service
- **8082**: REST Service
- Both services run on localhost in development

## Build and Runtime Process

```mermaid
graph LR
    subgraph "Build Time"
        XSD[XSD Schema<br/>order.xsd]
        WSDL[WSDL File<br/>orders.wsdl]
        
        XSD -->|Spring WS generates| WSDL
        WSDL -->|jaxws-maven-plugin| JAXWSCode[Generated<br/>JAX-WS Stubs]
        
        MapStructIF[MapStruct<br/>Interfaces]
        MapStructIF -->|mapstruct-processor| MapStructImpl[Generated<br/>Mapper Implementations]
    end
    
    subgraph "Runtime"
        JAXWSCode --> RestRuntime[REST Service<br/>Runtime]
        MapStructImpl --> RestRuntime
        
        XSD --> SoapRuntime[SOAP Service<br/>Runtime]
    end
    
    style XSD fill:#fff9c4
    style WSDL fill:#fff9c4
    style JAXWSCode fill:#c5e1a5
    style MapStructImpl fill:#c5e1a5
    style RestRuntime fill:#bbdefb
    style SoapRuntime fill:#f8bbd0
```

### Build Steps

1. **SOAP Service Build**
   - XSD schema loaded as resource
   - Spring WS generates WSDL at startup
   - JAXB generates classes from XSD (internal)
   
2. **REST Service Build**
   - Copy WSDL from SOAP service (manual step when schema changes)
   - `jaxws-maven-plugin` generates JAX-WS client stubs from WSDL
   - `mapstruct-processor` generates mapper implementations
   - Compile application code with generated classes
   
3. **Packaging**
   - Each service packaged as standalone JAR
   - Includes all dependencies (fat JAR)
   - Can run with `java -jar`

## Starting the Services

### Order of Startup
1. **Start SOAP Service First** (Port 8081)
   ```bash
   cd soap-service
   mvn spring-boot:run
   ```

2. **Start REST Service Second** (Port 8082)
   ```bash
   cd rest-service
   mvn spring-boot:run
   ```

### Health Check
- SOAP Service: `curl http://localhost:8081/ws/orders.wsdl`
- REST Service: `curl http://localhost:8082/v3/api-docs`

## Production Considerations

### Potential Production Setup

```mermaid
graph TB
    subgraph "Load Balancer"
        LB[Load Balancer]
    end
    
    subgraph "REST Service Cluster"
        REST1[REST Instance 1]
        REST2[REST Instance 2]
        REST3[REST Instance 3]
    end
    
    subgraph "SOAP Service Cluster"
        SOAP1[SOAP Instance 1]
        SOAP2[SOAP Instance 2]
    end
    
    subgraph "Data Layer"
        DB[(Database<br/>PostgreSQL/MySQL)]
        Cache[(Cache<br/>Redis)]
    end
    
    LB --> REST1
    LB --> REST2
    LB --> REST3
    
    REST1 --> SOAP1
    REST1 --> SOAP2
    REST2 --> SOAP1
    REST2 --> SOAP2
    REST3 --> SOAP1
    REST3 --> SOAP2
    
    SOAP1 --> DB
    SOAP2 --> DB
    SOAP1 --> Cache
    SOAP2 --> Cache
```

### Production Enhancements Needed
1. **Replace In-Memory Storage**: Use PostgreSQL/MySQL database
2. **Add Caching**: Redis for frequently accessed data
3. **Service Discovery**: Use Consul/Eureka for dynamic service locations
4. **Load Balancing**: Distribute traffic across multiple instances
5. **Monitoring**: Add metrics, logging, tracing (Prometheus, ELK, Zipkin)
6. **Security**: Add authentication, authorization, TLS/SSL
7. **Configuration**: Externalize configuration (Spring Cloud Config)
8. **Resilience**: Add circuit breakers, retry logic, timeouts
9. **Container Deployment**: Docker containers, Kubernetes orchestration
10. **API Gateway**: Add API gateway for rate limiting, routing

## Configuration Files

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

### SOAP Service - application.yml
```yaml
server:
  port: 8081

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
