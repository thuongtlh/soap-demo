# REST to SOAP Integration Demo

A Spring Boot 3.4.6 application demonstrating REST to SOAP integration with MapStruct mapping and OpenAPI documentation.

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
│  │   (REST)    │    │   (Logic)   │    │  (Mapping)  │    │ (Template)  │  │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘  │
│         │                                                        │          │
│         │                                                        │          │
│  ┌──────▼──────┐                                        ┌───────▼───────┐  │
│  │   OpenAPI   │                                        │ JAXB Classes  │  │
│  │ (Swagger UI)│                                        │(from XSD)     │  │
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
rest-soap-demo/
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
│               └── order.xsd                # XSD Schema definition
│
└── rest-service/                    # REST Frontend Service
    ├── pom.xml
    └── src/main/
        ├── java/com/demo/rest/
        │   ├── RestServiceApplication.java
        │   ├── config/
        │   │   ├── SoapClientConfig.java    # SOAP client configuration
        │   │   └── OpenApiConfig.java       # Swagger/OpenAPI configuration
        │   ├── controller/
        │   │   └── OrderController.java     # REST endpoints
        │   ├── service/
        │   │   └── OrderService.java        # Business logic
        │   ├── client/
        │   │   └── SoapOrderClient.java     # SOAP client
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
            └── xsd/
                └── order.xsd                # Same XSD for client generation
```

## Key Concepts Demonstrated

### 1. XSD Schema (Contract-First Design)

The `order.xsd` defines the SOAP message structure:

```xml
<!-- Request/Response types -->
<xs:element name="CreateOrderRequest">
    <xs:complexType>
        <xs:sequence>
            <xs:element name="customer" type="tns:CustomerType"/>
            <xs:element name="items" type="tns:OrderItemType" maxOccurs="unbounded"/>
            ...
        </xs:sequence>
    </xs:complexType>
</xs:element>
```

### 2. WSDL Generation

Spring WS auto-generates WSDL from XSD at runtime:
- **WSDL URL**: http://localhost:8081/ws/orders.wsdl
- **Endpoint**: http://localhost:8081/ws

### 3. JAXB Class Generation

Maven plugin generates Java classes from XSD:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>jaxb2-maven-plugin</artifactId>
    <configuration>
        <sources>
            <source>${project.basedir}/src/main/resources/xsd</source>
        </sources>
        <packageName>com.demo.rest.generated</packageName>
    </configuration>
</plugin>
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

OpenAPI provides:
- Interactive API documentation
- Request/response schema visualization
- Try-it-out functionality for testing
- Code generation for clients

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
5. SoapOrderClient sends SOAP request via WebServiceTemplate
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

## Technologies Used

| Technology | Purpose |
|------------|---------|
| Spring Boot 3.4.6 | Application framework |
| Spring Web Services | SOAP endpoint & client |
| JAXB | XML/Java binding |
| MapStruct 1.5.5 | Object mapping |
| Lombok | Boilerplate reduction |
| SpringDoc OpenAPI 2.6 | API documentation |
| Jakarta Validation | Request validation |

## Why These Technologies?

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

### Contract-First SOAP Design
- **Clear contract** - XSD defines exact message structure
- **Language-agnostic** - Any language can consume WSDL
- **Validation built-in** - XML schema validation
- **Versioning** - Namespace-based versioning
