# Create Order Sequence Diagram

This diagram illustrates the complete flow when a client creates an order through the REST API.

```mermaid
sequenceDiagram
    actor Client as REST Client
    participant Controller as OrderController
    participant Service as OrderService
    participant Mapper as OrderMapper
    participant SoapClient as SoapOrderClient
    participant Port as OrdersPort<br/>(JAX-WS)
    participant Endpoint as OrderEndpoint
    participant SoapService as OrderProcessingService
    participant Storage as In-Memory Storage
    
    Client->>+Controller: POST /api/v1/orders<br/>{CreateOrderRequestDto JSON}
    Note over Controller: @Valid validates request
    
    Controller->>+Service: createOrder(requestDto)
    Note over Service: Business logic orchestration
    
    Service->>+Mapper: toSoapCreateOrderRequest(requestDto)
    Note over Mapper: Convert REST DTO to SOAP Request<br/>- CustomerDto → CustomerType<br/>- OrderItemDto → OrderItemType<br/>- AddressDto → AddressType
    Mapper-->>-Service: CreateOrderRequest (SOAP)
    
    Service->>+SoapClient: createOrder(soapRequest)
    Note over SoapClient: JAX-WS client wrapper
    
    SoapClient->>+Port: createOrder(request)
    Note over Port: JAX-WS marshals to XML<br/>wraps in SOAP envelope
    
    Port->>+Endpoint: SOAP Request<br/>(HTTP POST with XML)
    Note over Endpoint: @PayloadRoot routes to handler
    
    Endpoint->>+SoapService: processCreateOrder(request)
    
    SoapService->>SoapService: Generate Order ID<br/>(ORD-XXXXXXXX)
    SoapService->>SoapService: Calculate Total Amount<br/>(sum of item prices)
    
    SoapService->>+Storage: Save Order
    Storage-->>-SoapService: Order Saved
    
    SoapService->>SoapService: Create Response<br/>- Set status to CONFIRMED<br/>- Calculate delivery date<br/>- Set timestamps
    
    SoapService-->>-Endpoint: CreateOrderResponse
    
    Endpoint-->>-Port: SOAP Response<br/>(XML in SOAP envelope)
    Note over Port: JAX-WS unmarshals XML<br/>to Java objects
    
    Port-->>-SoapClient: CreateOrderResponse (SOAP)
    
    SoapClient-->>-Service: CreateOrderResponse (SOAP)
    
    Service->>+Mapper: toCreateOrderResponseDto(soapResponse)
    Note over Mapper: Convert SOAP Response to REST DTO<br/>- OrderStatusType → String<br/>- XMLGregorianCalendar → LocalDateTime<br/>- XMLGregorianCalendar → LocalDate
    Mapper-->>-Service: CreateOrderResponseDto
    
    Service-->>-Controller: CreateOrderResponseDto
    
    Controller-->>-Client: 201 CREATED<br/>{CreateOrderResponseDto JSON}
    
    Note over Client,Storage: Complete round-trip:<br/>REST JSON → REST DTO → SOAP Types → XML<br/>→ SOAP Types → REST DTO → REST JSON
```

## Flow Steps Explained

### 1. Client Request
- Client sends HTTP POST request with JSON payload containing order details
- Request includes customer information, items, notes, and priority flag

### 2. Controller Layer
- **OrderController** receives the request
- Jakarta Bean Validation (`@Valid`) validates the request DTO
- Delegates to **OrderService**

### 3. Service Layer
- **OrderService** orchestrates the transformation and communication
- Calls **OrderMapper** to convert REST DTO to SOAP request

### 4. Mapping Layer
- **OrderMapper** uses MapStruct to perform the conversion
- Nested objects are mapped using specialized mappers:
  - **CustomerMapper** for customer data
  - **OrderItemMapper** for order items
  - **AddressMapper** for address information
- Result is a fully populated SOAP `CreateOrderRequest`

### 5. SOAP Client
- **SoapOrderClient** wraps the JAX-WS client
- Calls the type-safe **OrdersPort** interface
- JAX-WS handles:
  - Marshalling Java objects to XML
  - Wrapping XML in SOAP envelope
  - HTTP communication

### 6. SOAP Service
- **OrderEndpoint** receives the SOAP request
- Routes to appropriate handler based on `@PayloadRoot`
- Delegates to **OrderProcessingService**

### 7. Business Logic
- **OrderProcessingService** processes the order:
  - Generates unique order ID
  - Calculates total amount from items
  - Stores order in memory (simulating database)
  - Creates response with:
    - Order status (CONFIRMED)
    - Total amount
    - Estimated delivery date
    - Timestamps

### 8. Response Flow
- Response flows back through the same layers in reverse
- JAX-WS unmarshals SOAP response to Java objects
- **OrderMapper** converts SOAP response to REST DTO
- **OrderController** returns 201 CREATED with JSON response

## Data Transformations

### Request Path
```
REST JSON → CreateOrderRequestDto → CreateOrderRequest (SOAP) → XML
```

### Response Path
```
XML → CreateOrderResponse (SOAP) → CreateOrderResponseDto → REST JSON
```

## Error Handling

- Validation errors caught by `@Valid` annotation
- SOAP faults handled by **GlobalExceptionHandler**
- Returns appropriate HTTP status codes and error responses
