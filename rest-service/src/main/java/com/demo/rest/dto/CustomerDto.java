package com.demo.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST DTO for Customer information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Customer information")
public class CustomerDto {

    @NotBlank(message = "Customer ID is required")
    @Schema(description = "Unique customer identifier", example = "CUST-001")
    private String customerId;

    @NotBlank(message = "First name is required")
    @Schema(description = "Customer's first name", example = "John")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "Customer's last name", example = "Doe")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Schema(description = "Customer's email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Customer's phone number", example = "+1-555-123-4567")
    private String phone;

    @NotNull(message = "Shipping address is required")
    @Valid
    @Schema(description = "Shipping address")
    private AddressDto shippingAddress;

    @Valid
    @Schema(description = "Billing address (optional, defaults to shipping address)")
    private AddressDto billingAddress;
}
