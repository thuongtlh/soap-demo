# REST to SOAP Integration Demo

A Spring Boot 3.4.6 application demonstrating REST to SOAP integration with WSDL-first approach, MapStruct mapping, and OpenAPI documentation.

## Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              REST CLIENT                                     │
│                         (Postman, curl, etc.)                               │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    │ HTTP POST/GET (JSON)
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                         REST SERVICE (Port 8082)                            │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐  │
│  │  Controller │───▶│   Service   │───▶│  MapStruct  │───▶│ SOAP Client │  │
│  │   (REST)    │    │   (Logic)   │    │  (Mapping)  │    │  (JAX-WS)   │  │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘  │
│         │                                                        │          │
│         │                                                        │          │
│  ┌──────▼──────┐                                        ┌───────▼───────┐  │
│  │   OpenAPI   │                                        │ JAX-WS Stubs  │  │
│  │ (Swagger UI)│                                        │ (from WSDL)   │  │
│  └─────────────┘                                        └───────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    │ SOAP/XML
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                         SOAP SERVICE (Port 8081)                            │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────────────────────────┐  │
│  │  Endpoint   │───▶│   Service   │    │         WSDL/XSD                │  │
│  │   (SOAP)    │    │  (Logic)    │    │  (Contract-First Definition)   │  │
│  └─────────────┘    └─────────────┘    └─────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Project Structure

```
soap-demo/
├── pom.xml                          # Parent POM
├── soap-service/                    # SOAP Backend Service
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/demo/soap/
│       │   ├── SoapServiceApplication.java
│       │   ├── config/
│       │   │   └── WebServiceConfig.java    # WSDL & WS configuration
│       │   ├── endpoint/
│       │   │   └── OrderEndpoint.java       # SOAP endpoint
│       │   └── service/
│       │       └── OrderProcessingService.java
│       └── resources/
│           ├── application.yml
│           └── xsd/
│               └── order.xsd                # XSD Schema definition (source of truth)
│
└── rest-service/                    # REST Frontend Service
    ├── pom.xml
    └── src/main/
        ├── java/com/demo/rest/
        │   ├── RestServiceApplication.java
        │   ├── config/
        │   │   ├── SoapClientConfig.java    # JAX-WS client configuration
        │   │   └── OpenApiConfig.java       # Swagger/OpenAPI configuration
        │   ├── controller/
        │   │   └── OrderController.java     # REST endpoints
        │   ├── service/
        │   │   └── OrderService.java        # Business logic
        │   ├── client/
        │   │   └── SoapOrderClient.java     # JAX-WS SOAP client
        │   ├── mapper/                      # MapStruct mappers
        │   │   ├── OrderMapper.java
        │   │   ├── CustomerMapper.java
        │   │   ├── OrderItemMapper.java
        │   │   └── AddressMapper.java
        │   ├── dto/                         # REST DTOs
        │   │   ├── CreateOrderRequestDto.java
        │   │   ├── CreateOrderResponseDto.java
        │   │   └── ...
        │   └── exception/
        │       └── GlobalExceptionHandler.java
        └── resources/
            ├── application.yml
            └── wsdl/
                └── orders.wsdl              # Local WSDL for client generation
```

## Key Concepts Demonstrated

### 1. WSDL-First Approach

The REST service uses a **WSDL-first** approach for SOAP client generation:

- **Source of truth**: `soap-service/src/main/resources/xsd/order.xsd`
- **WSDL generation**: Spring WS auto-generates WSDL from XSD at runtime
- **Client generation**: `rest-service` uses a local copy of the WSDL to generate JAX-WS stubs

This approach eliminates XSD duplication between services.

### 2. JAX-WS Client Generation

Maven plugin generates Java client stubs from WSDL:

```xml
<plugin>
    <groupId>com.sun.xml.ws</groupId>
    <artifactId>jaxws-maven-plugin</artifactId>
    <version>4.0.2</version>
    <configuration>
        <wsdlDirectory>${project.basedir}/src/main/resources/wsdl</wsdlDirectory>
        <wsdlFiles>
            <wsdlFile>orders.wsdl</wsdlFile>
        </wsdlFiles>
        <packageName>com.demo.rest.generated</packageName>
    </configuration>
</plugin>
```

Generated classes include:
- `OrdersService` - JAX-WS service client
- `OrdersPort` - Type-safe SOAP operations interface
- `CreateOrderRequest`, `CreateOrderResponse`, etc. - JAXB data types

### 3. JAX-WS SOAP Client

The SOAP client uses generated JAX-WS stubs for type-safe SOAP calls:

```java
@Configuration
public class SoapClientConfig {

    @Bean
    public OrdersPort ordersPort(OrdersService ordersService) {
        OrdersPort port = ordersService.getOrdersPortSoap11();

        // Override endpoint URL from configuration
        BindingProvider bindingProvider = (BindingProvider) port;
        bindingProvider.getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                soapServiceUrl
        );

        return port;
    }
}
```

### 4. MapStruct Mapping

MapStruct generates efficient mapping code at compile time:

```java
@Mapper(componentModel = "spring", uses = {CustomerMapper.class, OrderItemMapper.class})
public interface OrderMapper {

    // REST DTO -> SOAP Request
    CreateOrderRequest toSoapCreateOrderRequest(CreateOrderRequestDto dto);

    // SOAP Response -> REST DTO
    @Mapping(target = "status", source = "status", qualifiedByName = "orderStatusToString")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "xmlCalendarToLocalDateTime")
    CreateOrderResponseDto toCreateOrderResponseDto(CreateOrderResponse soapResponse);
}
```

### 5. OpenAPI / Swagger Integration

REST API is documented using OpenAPI 3.0:
- **Swagger UI**: http://localhost:8082/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8082/v3/api-docs

## How to Run

### Prerequisites
- Java 17+
- Maven 3.8+

### Build the Project

```bash
# Build all modules
mvn clean install

# Or build each module separately
cd soap-service && mvn clean install
cd ../rest-service && mvn clean install
```

### Start the Services

**Terminal 1 - Start SOAP Service:**
```bash
cd soap-service
mvn spring-boot:run
```

**Terminal 2 - Start REST Service:**
```bash
cd rest-service
mvn spring-boot:run
```

### Test the API

#### Using Swagger UI
Open http://localhost:8082/swagger-ui.html in your browser.

#### Using curl

**Create Order:**
```bash
curl -X POST http://localhost:8082/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customer": {
      "customerId": "CUST-001",
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@example.com",
      "phone": "+1-555-123-4567",
      "shippingAddress": {
        "street": "123 Main Street",
        "city": "New York",
        "state": "NY",
        "zipCode": "10001",
        "country": "USA"
      }
    },
    "items": [
      {
        "productId": "PROD-001",
        "productName": "Wireless Headphones",
        "quantity": 2,
        "unitPrice": 49.99
      },
      {
        "productId": "PROD-002",
        "productName": "Phone Case",
        "quantity": 1,
        "unitPrice": 19.99
      }
    ],
    "notes": "Please gift wrap",
    "priority": false
  }'
```

**Get Order:**
```bash
curl http://localhost:8082/api/v1/orders/ORD-XXXXXXXX
```

### View WSDL

Access the auto-generated WSDL:
```bash
curl http://localhost:8081/ws/orders.wsdl
```

## Mapping Flow

```
1. REST Request (JSON) arrives at OrderController
         │
         ▼
2. @Valid validates CreateOrderRequestDto
         │
         ▼
3. OrderService receives the DTO
         │
         ▼
4. OrderMapper.toSoapCreateOrderRequest() converts:
   - CustomerDto → CustomerType (via CustomerMapper)
   - List<OrderItemDto> → List<OrderItemType> (via OrderItemMapper)
   - AddressDto → AddressType (via AddressMapper)
         │
         ▼
5. SoapOrderClient sends SOAP request via JAX-WS OrdersPort
         │
         ▼
6. SOAP Service processes and returns CreateOrderResponse
         │
         ▼
7. OrderMapper.toCreateOrderResponseDto() converts:
   - OrderStatusType → String
   - XMLGregorianCalendar → LocalDateTime/LocalDate
         │
         ▼
8. REST Response (JSON) returned to client
```

## Updating the WSDL

When the SOAP service's XSD changes:

1. Start the SOAP service
2. Fetch the updated WSDL:
   ```bash
   curl http://localhost:8081/ws/orders.wsdl > rest-service/src/main/resources/wsdl/orders.wsdl
   ```
3. Rebuild the REST service to regenerate JAX-WS stubs:
   ```bash
   cd rest-service && mvn clean compile
   ```

## Technologies Used

| Technology | Purpose |
|------------|---------|
| Spring Boot 3.4.6 | Application framework |
| Spring Web Services | SOAP endpoint (server-side) |
| JAX-WS 4.0.2 | SOAP client (WSDL-first) |
| JAXB | XML/Java binding |
| MapStruct 1.5.5 | Object mapping |
| Lombok | Boilerplate reduction |
| SpringDoc OpenAPI 2.8.4 | API documentation |
| Jakarta Validation | Request validation |

## Why These Technologies?

### WSDL-First over XSD Duplication
- **Single source of truth** - XSD only in SOAP service
- **Contract-driven** - Client generated from the actual service contract
- **Type-safe** - JAX-WS generates proper service interfaces
- **Maintainable** - No risk of XSD drift between services

### JAX-WS over Spring WebServiceTemplate
- **Standard Java EE API** - More portable
- **Type-safe operations** - `ordersPort.createOrder(request)` vs generic `marshalSendAndReceive()`
- **WSDL-first native** - Designed for WSDL-based code generation
- **Better tooling** - Industry-standard wsimport

### MapStruct over Manual Mapping
- **Compile-time code generation** - no reflection overhead
- **Type-safe mappings** - errors caught at compile time
- **Easy to customize** - `@Mapping` annotations for special cases
- **Nested object support** - uses other mappers automatically

### OpenAPI over WSDL for REST
- **Human-readable** - JSON/YAML format
- **Interactive documentation** - Swagger UI for testing
- **Modern tooling** - Code generators for many languages
- **REST-native** - Designed for RESTful APIs

## 📋 Documentation

- **[Main Documentation](docs/README.md)** - Architecture, sequence diagrams, and data models
- **[Technical Debt Analysis](docs/debt/)** - Comprehensive analysis of technical debt and scaling issues

### Technical Debt & Scaling

This is a **demo/prototype project**. For production deployment, see the [Technical Debt Documentation](docs/debt/) which identifies:

- **56 issues** across architecture, code quality, security, and observability
- **10 critical issues** that must be fixed before production
- **Estimated 14-16 weeks** minimum for production readiness
- **Production Readiness Score: 15/100**

**Key Issues:**
- ❌ No data persistence (in-memory only)
- ❌ No authentication or authorization
- ❌ No HTTPS/TLS encryption
- ❌ No test coverage (0%)
- ❌ No monitoring or health checks

See [docs/debt/README.md](docs/debt/README.md) for detailed analysis and roadmap.
