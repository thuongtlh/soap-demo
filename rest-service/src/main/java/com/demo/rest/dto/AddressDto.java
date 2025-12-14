package com.demo.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST DTO for Address information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Address information")
public class AddressDto {

    @NotBlank(message = "Street is required")
    @Schema(description = "Street address", example = "123 Main Street")
    private String street;

    @NotBlank(message = "City is required")
    @Schema(description = "City name", example = "New York")
    private String city;

    @NotBlank(message = "State is required")
    @Schema(description = "State or province", example = "NY")
    private String state;

    @NotBlank(message = "Zip code is required")
    @Schema(description = "ZIP or postal code", example = "10001")
    private String zipCode;

    @NotBlank(message = "Country is required")
    @Schema(description = "Country name", example = "USA")
    private String country;
}
