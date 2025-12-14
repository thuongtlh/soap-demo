package com.demo.rest.controller;

import com.demo.rest.dto.CreateOrderRequestDto;
import com.demo.rest.dto.CreateOrderResponseDto;
import com.demo.rest.dto.ErrorResponseDto;
import com.demo.rest.dto.GetOrderResponseDto;
import com.demo.rest.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Order operations.
 *
 * This controller:
 * 1. Exposes REST endpoints for order operations
 * 2. Validates incoming requests using Jakarta Bean Validation
 * 3. Delegates to OrderService for business logic
 * 4. Documents API using OpenAPI annotations
 *
 * The flow is:
 * REST Client -> Controller -> Service -> MapStruct -> SOAP Client -> SOAP Service
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management API - REST frontend for SOAP backend")
public class OrderController {

    private final OrderService orderService;

    /**
     * Create a new order.
     *
     * This endpoint:
     * 1. Receives a REST POST request with order details
     * 2. Validates the request using @Valid
     * 3. Calls the service which maps to SOAP and back
     * 4. Returns the order confirmation
     *
     * @param requestDto The order creation request
     * @return ResponseEntity with created order details
     */
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Create a new order",
            description = "Creates a new order by processing it through the SOAP backend service. " +
                    "The request is validated, mapped to SOAP format, sent to the backend, " +
                    "and the response is mapped back to REST format."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Order created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateOrderResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request - validation errors",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    public ResponseEntity<CreateOrderResponseDto> createOrder(
            @Valid @RequestBody CreateOrderRequestDto requestDto) {

        log.info("Received create order request for customer: {}",
                requestDto.getCustomer().getCustomerId());

        CreateOrderResponseDto response = orderService.createOrder(requestDto);

        log.info("Order created: {}", response.getOrderId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Get an existing order by ID.
     *
     * @param orderId The order ID to retrieve
     * @return ResponseEntity with order details
     */
    @GetMapping(
            value = "/{orderId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Get order by ID",
            description = "Retrieves order details from the SOAP backend service by order ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GetOrderResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    public ResponseEntity<GetOrderResponseDto> getOrder(
            @Parameter(description = "Order ID", example = "ORD-A1B2C3D4")
            @PathVariable String orderId) {

        log.info("Received get order request for orderId: {}", orderId);

        GetOrderResponseDto response = orderService.getOrder(orderId);

        return ResponseEntity.ok(response);
    }
}
