# Exercise 1: MapStruct Object Mapping

## Learning Objectives

By completing this exercise, you will:
- Understand how to create MapStruct mapper interfaces
- Learn to use `@Mapper` annotation with Spring component model
- Practice field mapping between DTOs and entity/SOAP types
- Implement custom mapping methods using `@Named` and `@Mapping` annotations
- Compose mappers using the `uses` attribute

## Prerequisites

Before starting this exercise, ensure you have:
- Basic understanding of Java interfaces and annotations
- Familiarity with Spring Boot dependency injection
- The project successfully building with `mvn clean compile`

## Background

This project uses MapStruct for converting between:
- **REST DTOs** (Data Transfer Objects used in REST API layer)
- **SOAP Types** (JAXB-generated classes from WSDL)

Examine the existing mappers in `rest-service/src/main/java/com/demo/rest/mapper/` to understand the patterns used.

---

## Task 1: Create a PaymentMapper

### Requirements

Create a new mapper class `PaymentMapper.java` that converts between `PaymentDto` (REST) and `PaymentType` (SOAP).

### Step 1: Create the DTO class

Create `PaymentDto.java` in `rest-service/src/main/java/com/demo/rest/dto/`:

```java
package com.demo.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private String paymentId;
    private String paymentMethod;  // e.g., "CREDIT_CARD", "PAYPAL", "BANK_TRANSFER"
    private BigDecimal amount;
    private String currency;       // e.g., "USD", "EUR"
    private String cardLastFourDigits;
    private String transactionReference;
}
```

### Step 2: Create the SOAP type class (simulation)

For this exercise, create `PaymentType.java` in `rest-service/src/main/java/com/demo/rest/generated/`:

```java
package com.demo.rest.generated;

import java.math.BigDecimal;

public class PaymentType {
    private String id;
    private String method;
    private BigDecimal totalAmount;
    private String currencyCode;
    private String cardNumber;
    private String txnRef;
    
    // Add getters and setters
}
```

### Step 3: Create the PaymentMapper

Create `PaymentMapper.java` in `rest-service/src/main/java/com/demo/rest/mapper/`:

**Your mapper should:**
1. Use `@Mapper(componentModel = "spring")`
2. Map fields with different names using `@Mapping`:
   - `paymentId` ↔ `id`
   - `paymentMethod` ↔ `method`
   - `amount` ↔ `totalAmount`
   - `currency` ↔ `currencyCode`
   - `cardLastFourDigits` ↔ `cardNumber`
   - `transactionReference` ↔ `txnRef`
3. Provide both `toSoapType(PaymentDto dto)` and `toDto(PaymentType soapType)` methods

### Expected Solution Structure

```java
@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "paymentId", target = "id")
    @Mapping(source = "paymentMethod", target = "method")
    // Add remaining mappings...
    PaymentType toSoapType(PaymentDto dto);

    @Mapping(source = "id", target = "paymentId")
    @Mapping(source = "method", target = "paymentMethod")
    // Add remaining mappings...
    PaymentDto toDto(PaymentType soapType);
}
```

---

## Task 2: Create a ShipmentMapper with Custom Type Conversion

### Requirements

Create a `ShipmentMapper.java` that handles date/time conversion.

### Step 1: Create the DTO class

Create `ShipmentDto.java` in `rest-service/src/main/java/com/demo/rest/dto/`:

```java
package com.demo.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentDto {
    private String shipmentId;
    private String trackingNumber;
    private String carrier;           // e.g., "UPS", "FEDEX", "DHL"
    private String status;            // e.g., "PENDING", "SHIPPED", "DELIVERED"
    private LocalDate estimatedDelivery;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
}
```

### Step 2: Create the SOAP type class (simulation)

Create `ShipmentType.java` in `rest-service/src/main/java/com/demo/rest/generated/`:

```java
package com.demo.rest.generated;

import javax.xml.datatype.XMLGregorianCalendar;

public class ShipmentType {
    private String shipmentId;
    private String trackingNumber;
    private String carrier;
    private ShipmentStatusType status;  // enum type
    private XMLGregorianCalendar estimatedDelivery;
    private XMLGregorianCalendar shippedAt;
    private XMLGregorianCalendar deliveredAt;
    
    // Add getters and setters
}
```

Create `ShipmentStatusType.java` enum:

```java
package com.demo.rest.generated;

public enum ShipmentStatusType {
    PENDING("PENDING"),
    SHIPPED("SHIPPED"),
    IN_TRANSIT("IN_TRANSIT"),
    DELIVERED("DELIVERED");
    
    private final String value;
    
    ShipmentStatusType(String value) {
        this.value = value;
    }
    
    public String value() {
        return value;
    }
}
```

### Step 3: Create the ShipmentMapper

**Your mapper should:**
1. Use `@Mapper(componentModel = "spring")`
2. Implement custom date conversions using `@Named` methods:
   - `XMLGregorianCalendar` → `LocalDate`
   - `XMLGregorianCalendar` → `LocalDateTime`
3. Implement enum to String conversion for status field
4. Use `@Mapping` with `qualifiedByName` for custom conversions

### Hints

Refer to `OrderMapper.java` for examples of:
- `@Named("xmlCalendarToLocalDateTime")` method
- `@Named("xmlCalendarToLocalDate")` method
- Using `qualifiedByName` in `@Mapping`

---

## Task 3: Create a DiscountMapper with Expression Mapping

### Requirements

Create a `DiscountMapper.java` that uses expression-based mapping to calculate derived fields.

### Step 1: Create the DTO class

Create `DiscountDto.java` in `rest-service/src/main/java/com/demo/rest/dto/`:

```java
package com.demo.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountDto {
    private String discountCode;
    private String discountType;      // "PERCENTAGE" or "FIXED"
    private BigDecimal discountValue; // Percentage (0-100) or fixed amount
    private BigDecimal originalPrice;
    private BigDecimal discountedPrice; // Calculated field
}
```

### Step 2: Create the SOAP type class (simulation)

Create `DiscountType.java` in `rest-service/src/main/java/com/demo/rest/generated/`:

```java
package com.demo.rest.generated;

import java.math.BigDecimal;

public class DiscountType {
    private String code;
    private String type;
    private BigDecimal value;
    private BigDecimal basePrice;
    private BigDecimal finalPrice;
    
    // Add getters and setters
}
```

### Step 3: Create the DiscountMapper

**Your mapper should:**
1. Use `@Mapper(componentModel = "spring")`
2. Map fields with different names
3. Implement a `default` method to calculate the discounted price:
   - For "PERCENTAGE": `finalPrice = originalPrice * (1 - discountValue/100)`
   - For "FIXED": `finalPrice = originalPrice - discountValue`
4. Use `@Mapping(target = "finalPrice", expression = "java(calculateFinalPrice(dto))")`

### Hints

Refer to `OrderItemMapper.java` (lines 32-33 and 66-74) for an example of:
- Using `expression` in `@Mapping` with the `calculateTotalPrice` method
- Implementing calculation logic in a `default` method that returns a computed value

---

## Task 4: Create a Composed Mapper (OrderSummaryMapper)

### Requirements

Create an `OrderSummaryMapper.java` that uses other mappers for nested object conversion.

### Step 1: Create the DTO class

Create `OrderSummaryDto.java` in `rest-service/src/main/java/com/demo/rest/dto/`:

```java
package com.demo.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDto {
    private String orderId;
    private CustomerDto customer;
    private List<OrderItemDto> items;
    private PaymentDto payment;
    private ShipmentDto shipment;
    private BigDecimal totalAmount;
    private String status;
}
```

### Step 2: Create the corresponding SOAP type

Create a matching `OrderSummaryType.java` with nested types.

### Step 3: Create the OrderSummaryMapper

**Your mapper should:**
1. Use `@Mapper(componentModel = "spring", uses = {CustomerMapper.class, OrderItemMapper.class, PaymentMapper.class, ShipmentMapper.class})`
2. MapStruct will automatically use the referenced mappers for nested objects
3. Implement list conversions for `items` field

### Hints

Refer to `OrderMapper.java` (line 31) and `CustomerMapper.java` (line 15) for examples of:
- Using the `uses` attribute to compose mappers (e.g., `uses = {CustomerMapper.class, OrderItemMapper.class}`)
- How MapStruct automatically delegates nested object mapping (notice how `CustomerMapper` uses `AddressMapper` for address fields)

---

## Verification Steps

After completing the exercises:

1. **Compile the project:**
   ```bash
   cd rest-service
   mvn clean compile
   ```
   
   MapStruct generates implementation classes during compilation. Check for errors.

2. **Verify generated mappers:**
   Look in `rest-service/target/generated-sources/annotations/com/demo/rest/mapper/` for the generated implementation classes.

3. **Write unit tests:**
   Create test classes to verify your mappers work correctly:
   ```java
   @SpringBootTest
   class PaymentMapperTest {
       @Autowired
       private PaymentMapper paymentMapper;
       
       @Test
       void shouldMapDtoToSoapType() {
           PaymentDto dto = PaymentDto.builder()
               .paymentId("PAY-001")
               .paymentMethod("CREDIT_CARD")
               .amount(new BigDecimal("99.99"))
               .currency("USD")
               .build();
               
           PaymentType soapType = paymentMapper.toSoapType(dto);
           
           assertEquals("PAY-001", soapType.getId());
           assertEquals("CREDIT_CARD", soapType.getMethod());
           assertEquals(new BigDecimal("99.99"), soapType.getTotalAmount());
       }
   }
   ```

---

## Submission Checklist

- [ ] `PaymentMapper.java` with field name mapping
- [ ] `ShipmentMapper.java` with custom date/time conversions
- [ ] `DiscountMapper.java` with expression-based calculation
- [ ] `OrderSummaryMapper.java` with composed mappers
- [ ] All corresponding DTO and Type classes
- [ ] Project compiles without errors
- [ ] Unit tests for each mapper (optional but recommended)

---

## Additional Resources

- [MapStruct Official Documentation](https://mapstruct.org/documentation/stable/reference/html/)
- [MapStruct GitHub Repository](https://github.com/mapstruct/mapstruct) - Examples and issue tracking
- [MapStruct with Spring Boot Guide](https://www.baeldung.com/mapstruct)
- Existing mappers in this project: `AddressMapper`, `CustomerMapper`, `OrderMapper`, `OrderItemMapper`
