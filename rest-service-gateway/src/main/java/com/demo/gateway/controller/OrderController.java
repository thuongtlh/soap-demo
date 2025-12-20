package com.demo.gateway.controller;

import com.demo.gateway.dto.CreateOrderRequestDto;
import com.demo.gateway.dto.CreateOrderResponseDto;
import com.demo.gateway.dto.ErrorResponseDto;
import com.demo.gateway.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Order operations.
 * Uses Gateway pattern to orchestrate multiple backend SOAP services.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management API with Gateway pattern")
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "Create order with inventory reservation",
            description = "Creates an order and reserves inventory. Orchestrates Order SOAP service and Inventory SOAP service."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Order created and inventory reserved",
                    content = @Content(schema = @Schema(implementation = CreateOrderResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Backend service unavailable",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping
    public ResponseEntity<CreateOrderResponseDto> createOrder(
            @Valid @RequestBody CreateOrderRequestDto request) {

        log.info("REST: Received create order request for customer: {}", request.getCustomer().getCustomerId());

        CreateOrderResponseDto response = orderService.createOrder(request);

        log.info("REST: Order created successfully. OrderId: {}", response.getOrderId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Create order without inventory",
            description = "Creates an order without inventory reservation. Only calls Order SOAP service."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Order created",
                    content = @Content(schema = @Schema(implementation = CreateOrderResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping("/simple")
    public ResponseEntity<CreateOrderResponseDto> createOrderSimple(
            @Valid @RequestBody CreateOrderRequestDto request) {

        log.info("REST: Received simple create order request for customer: {}", request.getCustomer().getCustomerId());

        CreateOrderResponseDto response = orderService.createOrderSimple(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
