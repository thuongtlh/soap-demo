package com.demo.gateway.client;

import com.demo.gateway.generated.order.CreateOrderRequest;
import com.demo.gateway.generated.order.CreateOrderResponse;
import com.demo.gateway.generated.order.GetOrderRequest;
import com.demo.gateway.generated.order.GetOrderResponse;
import com.demo.gateway.generated.order.OrdersPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * SOAP Client for Order Service with resilience patterns.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSoapClient {

    private final OrdersPort ordersPort;

    @CircuitBreaker(name = "orderService", fallbackMethod = "createOrderFallback")
    @Retry(name = "orderService")
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        log.info("Calling Order SOAP service - createOrder for customer: {}",
                request.getCustomer().getCustomerId());

        CreateOrderResponse response = ordersPort.createOrder(request);

        log.info("Order SOAP service responded with orderId: {}", response.getOrderId());
        return response;
    }

    @CircuitBreaker(name = "orderService", fallbackMethod = "getOrderFallback")
    @Retry(name = "orderService")
    public GetOrderResponse getOrder(GetOrderRequest request) {
        log.info("Calling Order SOAP service - getOrder for orderId: {}", request.getOrderId());

        GetOrderResponse response = ordersPort.getOrder(request);

        log.info("Order SOAP service responded for orderId: {}", response.getOrderId());
        return response;
    }

    // Fallback methods
    private CreateOrderResponse createOrderFallback(CreateOrderRequest request, Throwable t) {
        log.error("Order service unavailable, fallback triggered for createOrder: {}", t.getMessage());
        throw new RuntimeException("Order service is currently unavailable. Please try again later.", t);
    }

    private GetOrderResponse getOrderFallback(GetOrderRequest request, Throwable t) {
        log.error("Order service unavailable, fallback triggered for getOrder: {}", t.getMessage());
        throw new RuntimeException("Order service is currently unavailable. Please try again later.", t);
    }
}
