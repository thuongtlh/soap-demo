package com.demo.soap.endpoint;

import com.demo.soap.config.WebServiceConfig;
import com.demo.soap.generated.*;
import com.demo.soap.service.OrderProcessingService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 * SOAP Endpoint for Order operations.
 *
 * This endpoint handles incoming SOAP requests and delegates to the service layer.
 * It uses the @PayloadRoot annotation to route requests based on namespace and local part.
 */
@Endpoint
public class OrderEndpoint {

    private final OrderProcessingService orderProcessingService;

    public OrderEndpoint(OrderProcessingService orderProcessingService) {
        this.orderProcessingService = orderProcessingService;
    }

    /**
     * Handle CreateOrder SOAP request.
     *
     * @param request The SOAP request containing order details
     * @return CreateOrderResponse with order confirmation
     */
    @PayloadRoot(namespace = WebServiceConfig.NAMESPACE_URI, localPart = "CreateOrderRequest")
    @ResponsePayload
    public CreateOrderResponse createOrder(@RequestPayload CreateOrderRequest request) {
        return orderProcessingService.processCreateOrder(request);
    }

    /**
     * Handle GetOrder SOAP request.
     *
     * @param request The SOAP request containing order ID
     * @return GetOrderResponse with order details
     */
    @PayloadRoot(namespace = WebServiceConfig.NAMESPACE_URI, localPart = "GetOrderRequest")
    @ResponsePayload
    public GetOrderResponse getOrder(@RequestPayload GetOrderRequest request) {
        return orderProcessingService.processGetOrder(request);
    }
}
