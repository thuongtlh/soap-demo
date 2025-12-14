package com.demo.soap.service;

import com.demo.soap.generated.*;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service layer for processing SOAP order requests.
 *
 * This service simulates order processing logic:
 * - Validates order data
 * - Calculates totals
 * - Stores orders in memory (for demo purposes)
 * - Returns appropriate responses
 */
@Service
public class OrderProcessingService {

    // In-memory storage for demo purposes
    private final Map<String, CreateOrderRequest> orderStorage = new ConcurrentHashMap<>();
    private final Map<String, CreateOrderResponse> orderResponseStorage = new ConcurrentHashMap<>();

    /**
     * Process a CreateOrder request.
     *
     * @param request The incoming SOAP request
     * @return CreateOrderResponse with order confirmation
     */
    public CreateOrderResponse processCreateOrder(CreateOrderRequest request) {
        // Generate unique order ID
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Calculate total amount from items
        BigDecimal totalAmount = calculateTotalAmount(request);

        // Store the order (simulate database save)
        orderStorage.put(orderId, request);

        // Build response
        CreateOrderResponse response = new CreateOrderResponse();
        response.setOrderId(orderId);
        response.setStatus(OrderStatusType.CONFIRMED);
        response.setMessage("Order successfully created and confirmed");
        response.setTotalAmount(totalAmount);
        response.setCreatedAt(toXMLGregorianCalendar(LocalDateTime.now()));

        // Set estimated delivery date (5 business days for regular, 2 for priority)
        int daysToAdd = request.isPriority() ? 2 : 5;
        response.setEstimatedDeliveryDate(toXMLGregorianCalendarDate(LocalDate.now().plusDays(daysToAdd)));

        // Store response for GetOrder
        orderResponseStorage.put(orderId, response);

        System.out.println("SOAP Service: Created order " + orderId + " with total: $" + totalAmount);

        return response;
    }

    /**
     * Process a GetOrder request.
     *
     * @param request The incoming SOAP request with order ID
     * @return GetOrderResponse with order details
     */
    public GetOrderResponse processGetOrder(GetOrderRequest request) {
        String orderId = request.getOrderId();

        CreateOrderRequest originalRequest = orderStorage.get(orderId);
        CreateOrderResponse originalResponse = orderResponseStorage.get(orderId);

        if (originalRequest == null || originalResponse == null) {
            throw new OrderNotFoundException("Order not found: " + orderId);
        }

        // Build GetOrderResponse
        GetOrderResponse response = new GetOrderResponse();
        response.setOrderId(orderId);
        response.setCustomer(originalRequest.getCustomer());
        response.getItems().addAll(originalRequest.getItems());
        response.setStatus(originalResponse.getStatus());
        response.setTotalAmount(originalResponse.getTotalAmount());
        response.setNotes(originalRequest.getNotes());
        response.setCreatedAt(originalResponse.getCreatedAt());
        response.setUpdatedAt(toXMLGregorianCalendar(LocalDateTime.now()));

        System.out.println("SOAP Service: Retrieved order " + orderId);

        return response;
    }

    /**
     * Calculate total amount from order items.
     */
    private BigDecimal calculateTotalAmount(CreateOrderRequest request) {
        return request.getItems().stream()
                .map(OrderItemType::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Convert LocalDateTime to XMLGregorianCalendar.
     */
    private XMLGregorianCalendar toXMLGregorianCalendar(LocalDateTime dateTime) {
        try {
            GregorianCalendar gc = GregorianCalendar.from(dateTime.atZone(ZoneId.systemDefault()));
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Failed to create XMLGregorianCalendar", e);
        }
    }

    /**
     * Convert LocalDate to XMLGregorianCalendar (date only).
     */
    private XMLGregorianCalendar toXMLGregorianCalendarDate(LocalDate date) {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    date.getYear(),
                    date.getMonthValue(),
                    date.getDayOfMonth(),
                    0, 0, 0, 0, 0
            );
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Failed to create XMLGregorianCalendar", e);
        }
    }

    /**
     * Custom exception for order not found.
     */
    public static class OrderNotFoundException extends RuntimeException {
        public OrderNotFoundException(String message) {
            super(message);
        }
    }
}
