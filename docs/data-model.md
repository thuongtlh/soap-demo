# Data Model Diagram

This diagram shows the domain objects, DTOs, and their relationships in both REST and SOAP layers.

## REST Layer DTOs

```mermaid
classDiagram
    class CreateOrderRequestDto {
        +CustomerDto customer
        +List~OrderItemDto~ items
        +String notes
        +boolean priority
    }
    
    class CreateOrderResponseDto {
        +String orderId
        +String status
        +String message
        +BigDecimal totalAmount
        +LocalDate estimatedDeliveryDate
        +LocalDateTime createdAt
    }
    
    class GetOrderResponseDto {
        +String orderId
        +CustomerDto customer
        +List~OrderItemDto~ items
        +String status
        +BigDecimal totalAmount
        +String notes
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
    }
    
    class CustomerDto {
        +String customerId
        +String firstName
        +String lastName
        +String email
        +String phone
        +AddressDto shippingAddress
        +AddressDto billingAddress
    }
    
    class AddressDto {
        +String street
        +String city
        +String state
        +String zipCode
        +String country
    }
    
    class OrderItemDto {
        +String productId
        +String productName
        +int quantity
        +BigDecimal unitPrice
        +BigDecimal totalPrice
    }
    
    class ErrorResponseDto {
        +String timestamp
        +int status
        +String error
        +String message
        +String path
    }
    
    CreateOrderRequestDto --> CustomerDto
    CreateOrderRequestDto --> OrderItemDto
    CreateOrderResponseDto ..> CreateOrderRequestDto : responds to
    GetOrderResponseDto --> CustomerDto
    GetOrderResponseDto --> OrderItemDto
    CustomerDto --> AddressDto
```

## SOAP Layer Types (Generated from XSD)

```mermaid
classDiagram
    class CreateOrderRequest {
        +CustomerType customer
        +List~OrderItemType~ items
        +String notes
        +boolean priority
    }
    
    class CreateOrderResponse {
        +String orderId
        +OrderStatusType status
        +String message
        +BigDecimal totalAmount
        +XMLGregorianCalendar estimatedDeliveryDate
        +XMLGregorianCalendar createdAt
    }
    
    class GetOrderRequest {
        +String orderId
    }
    
    class GetOrderResponse {
        +String orderId
        +CustomerType customer
        +List~OrderItemType~ items
        +OrderStatusType status
        +BigDecimal totalAmount
        +String notes
        +XMLGregorianCalendar createdAt
        +XMLGregorianCalendar updatedAt
    }
    
    class CustomerType {
        +String customerId
        +String firstName
        +String lastName
        +String email
        +String phone
        +AddressType shippingAddress
        +AddressType billingAddress
    }
    
    class AddressType {
        +String street
        +String city
        +String state
        +String zipCode
        +String country
    }
    
    class OrderItemType {
        +String productId
        +String productName
        +int quantity
        +BigDecimal unitPrice
        +BigDecimal totalPrice
    }
    
    class OrderStatusType {
        <<enumeration>>
        PENDING
        CONFIRMED
        PROCESSING
        SHIPPED
        DELIVERED
        CANCELLED
        FAILED
    }
    
    class ServiceFault {
        +String errorCode
        +String errorMessage
        +XMLGregorianCalendar timestamp
    }
    
    CreateOrderRequest --> CustomerType
    CreateOrderRequest --> OrderItemType
    CreateOrderResponse --> OrderStatusType
    GetOrderRequest ..> GetOrderResponse : requests
    GetOrderResponse --> CustomerType
    GetOrderResponse --> OrderItemType
    GetOrderResponse --> OrderStatusType
    CustomerType --> AddressType
```

## Mapping Relationships

```mermaid
graph LR
    subgraph "REST DTOs"
        RCreateReq[CreateOrderRequestDto]
        RCreateRes[CreateOrderResponseDto]
        RGetRes[GetOrderResponseDto]
        RCustomer[CustomerDto]
        RAddress[AddressDto]
        RItem[OrderItemDto]
    end
    
    subgraph "MapStruct Mappers"
        OrderMapper[OrderMapper]
        CustomerMapper[CustomerMapper]
        AddressMapper[AddressMapper]
        ItemMapper[OrderItemMapper]
    end
    
    subgraph "SOAP Types"
        SCreateReq[CreateOrderRequest]
        SCreateRes[CreateOrderResponse]
        SGetRes[GetOrderResponse]
        SCustomer[CustomerType]
        SAddress[AddressType]
        SItem[OrderItemType]
    end
    
    RCreateReq -->|OrderMapper| SCreateReq
    SCreateRes -->|OrderMapper| RCreateRes
    SGetRes -->|OrderMapper| RGetRes
    
    RCustomer -->|CustomerMapper| SCustomer
    SCustomer -->|CustomerMapper| RCustomer
    
    RAddress -->|AddressMapper| SAddress
    SAddress -->|AddressMapper| RAddress
    
    RItem -->|ItemMapper| SItem
    SItem -->|ItemMapper| RItem
    
    style OrderMapper fill:#b3e5fc
    style CustomerMapper fill:#b3e5fc
    style AddressMapper fill:#b3e5fc
    style ItemMapper fill:#b3e5fc
```

## Object Relationships and Compositions

```mermaid
graph TB
    subgraph "Order Creation Request Flow"
        CreateReq[CreateOrderRequestDto]
        CreateReq --> Customer1[CustomerDto]
        CreateReq --> Items1[List of OrderItemDto]
        Customer1 --> ShipAddr1[Shipping AddressDto]
        Customer1 --> BillAddr1[Billing AddressDto]
    end
    
    subgraph "Order Creation Response Flow"
        CreateRes[CreateOrderResponseDto]
        CreateRes -.contains.-> OrderId1[Order ID]
        CreateRes -.contains.-> Status1[Status String]
        CreateRes -.contains.-> Total1[Total Amount]
        CreateRes -.contains.-> Dates1[Delivery Date & Created At]
    end
    
    subgraph "Order Retrieval Response Flow"
        GetRes[GetOrderResponseDto]
        GetRes --> Customer2[CustomerDto]
        GetRes --> Items2[List of OrderItemDto]
        GetRes -.contains.-> OrderId2[Order ID]
        GetRes -.contains.-> Status2[Status String]
        GetRes -.contains.-> Total2[Total Amount]
        GetRes -.contains.-> Dates2[Created At & Updated At]
        Customer2 --> ShipAddr2[Shipping AddressDto]
        Customer2 --> BillAddr2[Billing AddressDto]
    end
    
    style Customer1 fill:#ffe0b2
    style Customer2 fill:#ffe0b2
    style Items1 fill:#c8e6c9
    style Items2 fill:#c8e6c9
    style ShipAddr1 fill:#f8bbd0
    style ShipAddr2 fill:#f8bbd0
    style BillAddr1 fill:#f8bbd0
    style BillAddr2 fill:#f8bbd0
```

## Key Data Model Features

### REST Layer (DTOs)
- **Clean Java 8 Types**: Uses `LocalDateTime`, `LocalDate`, `BigDecimal`
- **Jakarta Validation**: Annotated with `@Valid`, `@NotNull`, `@Email`, etc.
- **Lombok**: Uses `@Data`, `@Builder` for boilerplate reduction
- **Immutability Option**: Can use `@Value` for immutable DTOs
- **JSON Serialization**: Works seamlessly with Jackson

### SOAP Layer (Generated Types)
- **JAXB Annotated**: Auto-generated with `@XmlRootElement`, `@XmlType`, etc.
- **XML Types**: Uses `XMLGregorianCalendar` for dates
- **Enum Support**: `OrderStatusType` enum for type-safe status values
- **Contract-Driven**: Generated directly from XSD schema
- **Namespace Aware**: Includes XML namespace declarations

### Mapping Considerations

#### Type Conversions
- **Date/Time**: `XMLGregorianCalendar` ↔ `LocalDateTime` / `LocalDate`
- **Enums**: `OrderStatusType` ↔ `String`
- **Nested Objects**: Recursive mapping via specialized mappers
- **Collections**: `List<OrderItemType>` ↔ `List<OrderItemDto>`

#### Mapper Hierarchy
1. **OrderMapper**: Top-level mapper, uses other mappers
2. **CustomerMapper**: Handles customer data, uses AddressMapper
3. **AddressMapper**: Simple field-to-field mapping
4. **OrderItemMapper**: Handles order item conversion

#### Custom Mapping Methods
- `orderStatusToString()`: Converts enum to string
- `xmlCalendarToLocalDateTime()`: Converts XML datetime to Java 8 datetime
- `xmlCalendarToLocalDate()`: Converts XML date to Java 8 date

### Validation Rules

#### CreateOrderRequestDto
- Customer: Required, must be valid CustomerDto
- Items: Required, at least 1 item
- Each item: Valid product info, positive quantity and prices
- Notes: Optional
- Priority: Defaults to false

#### CustomerDto
- Customer ID: Required, non-empty
- First Name: Required
- Last Name: Required
- Email: Required, valid email format
- Phone: Optional
- Shipping Address: Required
- Billing Address: Optional (defaults to shipping if not provided)

#### AddressDto
- All fields required: street, city, state, zipCode, country
- ZipCode: Must match pattern (varies by country)

#### OrderItemDto
- Product ID: Required
- Product Name: Required
- Quantity: Positive integer
- Unit Price: Positive decimal
- Total Price: Calculated as quantity × unit price

### XSD-Defined Constraints

The XSD schema defines:
- Required vs optional fields (`minOccurs`)
- Data types (string, int, decimal, boolean, date, dateTime)
- Enumerations (OrderStatusType)
- Complex type compositions
- Cardinality (single vs collections)

These constraints are enforced by:
1. **XSD validation** in SOAP layer
2. **Jakarta Bean Validation** in REST layer
3. **MapStruct compilation checks** during build
