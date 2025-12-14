package com.demo.rest.service;

import com.demo.rest.client.SoapOrderClient;
import com.demo.rest.dto.CreateOrderRequestDto;
import com.demo.rest.dto.CreateOrderResponseDto;
import com.demo.rest.dto.GetOrderResponseDto;
import com.demo.rest.generated.CreateOrderRequest;
import com.demo.rest.generated.CreateOrderResponse;
import com.demo.rest.generated.GetOrderResponse;
import com.demo.rest.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service layer for Order operations.
 *
 * This service orchestrates the flow:
 * 1. Receives REST DTO from controller
 * 2. Uses MapStruct to convert to SOAP request
 * 3. Calls SOAP service via client
 * 4. Uses MapStruct to convert SOAP response to REST DTO
 * 5. Returns REST DTO to controller
 *
 * This demonstrates the typical integration pattern:
 * REST API -> Service -> Mapper -> SOAP Client -> SOAP Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final SoapOrderClient soapOrderClient;
    private final OrderMapper orderMapper;

    /**
     * Create a new order.
     *
     * Flow:
     * 1. REST DTO (CreateOrderRequestDto) comes in
     * 2. MapStruct converts to SOAP request (CreateOrderRequest)
     * 3. SOAP client sends to SOAP service
     * 4. SOAP response (CreateOrderResponse) received
     * 5. MapStruct converts to REST DTO (CreateOrderResponseDto)
     *
     * @param requestDto The REST request from the client
     * @return The REST response for the client
     */
    public CreateOrderResponseDto createOrder(CreateOrderRequestDto requestDto) {
        log.info("Processing create order request for customer: {}",
                requestDto.getCustomer().getCustomerId());

        // Step 1: Map REST DTO -> SOAP Request using MapStruct
        log.debug("Mapping REST DTO to SOAP request...");
        CreateOrderRequest soapRequest = orderMapper.toSoapCreateOrderRequest(requestDto);

        // Step 2: Call SOAP Service
        log.debug("Calling SOAP service...");
        CreateOrderResponse soapResponse = soapOrderClient.createOrder(soapRequest);

        // Step 3: Map SOAP Response -> REST DTO using MapStruct
        log.debug("Mapping SOAP response to REST DTO...");
        CreateOrderResponseDto responseDto = orderMapper.toCreateOrderResponseDto(soapResponse);

        log.info("Order created successfully with ID: {}", responseDto.getOrderId());

        return responseDto;
    }

    /**
     * Get an existing order by ID.
     *
     * Flow:
     * 1. Order ID comes in from REST controller
     * 2. SOAP client sends GetOrder request
     * 3. SOAP response (GetOrderResponse) received
     * 4. MapStruct converts to REST DTO (GetOrderResponseDto)
     *
     * @param orderId The order ID to retrieve
     * @return The REST response with order details
     */
    public GetOrderResponseDto getOrder(String orderId) {
        log.info("Processing get order request for orderId: {}", orderId);

        // Step 1: Call SOAP Service
        log.debug("Calling SOAP service...");
        GetOrderResponse soapResponse = soapOrderClient.getOrder(orderId);

        // Step 2: Map SOAP Response -> REST DTO using MapStruct
        log.debug("Mapping SOAP response to REST DTO...");
        GetOrderResponseDto responseDto = orderMapper.toGetOrderResponseDto(soapResponse);

        log.info("Order retrieved successfully: {}", orderId);

        return responseDto;
    }
}
