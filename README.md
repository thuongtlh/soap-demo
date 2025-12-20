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

## Documentation

📚 **Comprehensive architecture documentation is available in the [docs](./docs) folder.**

The [docs folder](./docs) contains detailed Mermaid diagrams and explanations for:
- [System Architecture](./docs/system-architecture.md) - High-level system overview
- [Component Diagram](./docs/component-diagram.md) - Detailed component relationships
- [Data Model](./docs/data-model.md) - Complete data structures and mappings
- [Technology Stack](./docs/technology-stack.md) - Framework dependencies and versions
- [Create Order Sequence](./docs/create-order-sequence.md) - Order creation flow
- [Get Order Sequence](./docs/get-order-sequence.md) - Order retrieval flow
- [Deployment Architecture](./docs/deployment-architecture.md) - Deployment and runtime configuration

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

### 3. Understanding JAXB and JAX-WS

This project uses two key Java technologies for SOAP integration:

#### JAXB (Java Architecture for XML Binding)

**JAXB** handles the conversion between XML and Java objects (marshalling/unmarshalling):

- **XML → Java (Unmarshalling)**: Converts incoming SOAP XML messages into Java objects
- **Java → XML (Marshalling)**: Converts Java objects into outgoing SOAP XML messages
- **Annotations**: Generated classes use JAXB annotations like:
  - `@XmlRootElement` - Marks the root element of the XML
  - `@XmlType` - Defines complex types with properties
  - `@XmlElement` - Maps fields to XML elements
  - `@XmlAccessorType` - Controls field/property access

**Example of JAXB-annotated class:**
```java
@XmlRootElement(name = "CreateOrderRequest")
@XmlType(propOrder = {"customer", "items", "notes", "priority"})
public class CreateOrderRequest {
    @XmlElement(required = true)
    private CustomerType customer;
    
    @XmlElement(required = true)
    private List<OrderItemType> items;
    
    private String notes;
    private boolean priority;
}
```

**Jakarta namespace**: Modern JAXB uses `jakarta.xml.bind.*` packages instead of the legacy `javax.xml.bind.*` (Jakarta EE 9+ migration).

#### JAX-WS (Java API for XML Web Services)

**JAX-WS** provides the SOAP client runtime that handles web service communication:

- **Creates service proxy**: Generates `OrdersPort` interface for type-safe method calls
- **Builds SOAP envelopes**: Wraps JAXB-marshalled objects in SOAP structure
- **Sends HTTP requests**: Handles the HTTP/HTTPS transport layer
- **Processes SOAP responses**: Unwraps SOAP envelopes and unmarshalls response data
- **Handles SOAP faults**: Converts SOAP faults into Java exceptions

**Key JAX-WS components:**
```java
// Service class - entry point for client
OrdersService service = new OrdersService();

// Port interface - type-safe operations
OrdersPort port = service.getOrdersPortSoap11();

// BindingProvider - runtime configuration
BindingProvider provider = (BindingProvider) port;
provider.getRequestContext().put(
    BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
    "http://localhost:8081/ws"
);

// Type-safe method call - JAX-WS handles SOAP details
CreateOrderResponse response = port.createOrder(request);
```

**Annotations:**
- `@WebService` - Marks a service interface or implementation
- `@WebMethod` - Defines a web service operation
- `@SOAPBinding(parameterStyle = BARE)` - Parameters map directly to SOAP body parts
- `@XmlSeeAlso(ObjectFactory.class)` - References the JAXB object factory for type context

#### The Relationship: JAXB + JAX-WS

```
┌─────────────────────────────────────────────────────────────┐
│                      Your Java Code                         │
│  CreateOrderRequest request = new CreateOrderRequest();     │
│  CreateOrderResponse response = port.createOrder(request);  │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                        JAX-WS Layer                         │
│  • Builds SOAP envelope structure                           │
│  • Manages HTTP connection                                  │
│  • Handles SOAP headers and faults                          │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                        JAXB Layer                           │
│  • Marshalls Java objects → XML                             │
│  • Unmarshalls XML → Java objects                           │
│  • Uses annotations to map fields                           │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
              SOAP XML over HTTP to server
```

#### Understanding "Binding"

The term **"binding"** has different meanings in this context:

1. **XML Binding (JAXB)**: How XML schema types are bound/mapped to Java classes
   - Example: XSD `complexType` → Java class with `@XmlType`
   - Example: XSD `string` → Java `String`

2. **SOAP Binding (JAX-WS)**: How SOAP operations are bound/mapped to Java methods
   - **Document/Literal**: SOAP body contains XML documents (most common)
   - **RPC/Encoded**: SOAP body contains encoded method calls (legacy)
   - **Parameter Style BARE**: Request object maps directly to SOAP body
   - **Parameter Style WRAPPED**: Parameters wrapped in a request element

**Example from this project:**
```java
@WebService
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface OrdersPort {
    // BARE style: CreateOrderRequest directly in SOAP body
    CreateOrderResponse createOrder(CreateOrderRequest request);
}
```

#### Code Generation Flow

```
┌──────────────┐
│   XSD Schema │  ← Source of truth (order.xsd)
└──────┬───────┘
       │
       ▼ (Spring WS generates WSDL at runtime)
┌──────────────┐
│     WSDL     │  ← Service contract (orders.wsdl)
└──────┬───────┘
       │
       ▼ (jaxws-maven-plugin: wsimport)
┌──────────────────────────────────┐
│   Generated Java Classes         │
│  • OrdersService (JAX-WS)        │
│  • OrdersPort (JAX-WS)           │
│  • CreateOrderRequest (JAXB)     │
│  • CreateOrderResponse (JAXB)    │
│  • CustomerType (JAXB)           │
│  • OrderItemType (JAXB)          │
│  • ObjectFactory (JAXB)          │
└──────────────────────────────────┘
```

The **ObjectFactory** class is a JAXB helper that creates instances of generated types with proper XML element wrapping.

### 4. JAX-WS SOAP Client

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

### 5. MapStruct Mapping

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

### 6. OpenAPI / Swagger Integration

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
| JAX-WS 4.0.2 | SOAP client runtime (WSDL-first) |
| JAXB (Jakarta XML Binding) | XML ↔ Java object binding (marshalling/unmarshalling) |
| MapStruct 1.5.5 | Object mapping |
| Lombok | Boilerplate reduction |
| SpringDoc OpenAPI 2.8.4 | API documentation |
| Jakarta Validation | Request validation |

**Note**: This project uses Jakarta EE packages (`jakarta.xml.bind.*`, `jakarta.xml.ws.*`) instead of legacy Java EE packages (`javax.*`).

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
- **JAXB integration** - Seamless XML binding with generated types

### JAXB for XML Binding
- **Automatic code generation** - Java classes generated from XSD/WSDL
- **Annotation-driven** - Declarative mapping with `@XmlElement`, `@XmlType`, etc.
- **Jakarta namespace** - Modern `jakarta.xml.bind.*` packages (Jakarta EE 9+)
- **Type-safe XML** - Compile-time validation of XML structure
- **Bidirectional** - Marshalling (Java → XML) and unmarshalling (XML → Java)

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
