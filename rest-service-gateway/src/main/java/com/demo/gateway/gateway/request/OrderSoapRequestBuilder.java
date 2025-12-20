package com.demo.gateway.gateway.request;

import com.demo.gateway.dto.AddressDto;
import com.demo.gateway.dto.CustomerDto;
import com.demo.gateway.dto.OrderItemDto;
import com.demo.gateway.generated.order.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Builder for creating SOAP Order Service requests from gateway requests.
 * Handles transformation from gateway model to SOAP generated classes.
 */
@Slf4j
@Component
public class OrderSoapRequestBuilder {

    /**
     * Build SOAP CreateOrderRequest from gateway request.
     */
    public CreateOrderRequest buildCreateOrderRequest(OrderGatewayRequest gatewayRequest) {
        log.debug("Building SOAP CreateOrderRequest for customer: {}",
                gatewayRequest.getCustomer().getCustomerId());

        CreateOrderRequest soapRequest = new CreateOrderRequest();
        soapRequest.setCustomer(buildCustomerType(gatewayRequest.getCustomer()));
        soapRequest.setNotes(gatewayRequest.getNotes());
        soapRequest.setPriority(gatewayRequest.isPriority());

        gatewayRequest.getItems().forEach(item ->
                soapRequest.getItems().add(buildOrderItemType(item)));

        return soapRequest;
    }

    /**
     * Build SOAP GetOrderRequest.
     */
    public GetOrderRequest buildGetOrderRequest(String orderId) {
        GetOrderRequest request = new GetOrderRequest();
        request.setOrderId(orderId);
        return request;
    }

    private CustomerType buildCustomerType(CustomerDto dto) {
        CustomerType customer = new CustomerType();
        customer.setCustomerId(dto.getCustomerId());
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setShippingAddress(buildAddressType(dto.getShippingAddress()));
        if (dto.getBillingAddress() != null) {
            customer.setBillingAddress(buildAddressType(dto.getBillingAddress()));
        }
        return customer;
    }

    private AddressType buildAddressType(AddressDto dto) {
        AddressType address = new AddressType();
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setZipCode(dto.getZipCode());
        address.setCountry(dto.getCountry());
        return address;
    }

    private OrderItemType buildOrderItemType(OrderItemDto dto) {
        OrderItemType item = new OrderItemType();
        item.setProductId(dto.getProductId());
        item.setProductName(dto.getProductName());
        item.setQuantity(dto.getQuantity());
        item.setUnitPrice(dto.getUnitPrice());
        item.setTotalPrice(calculateTotalPrice(dto));
        return item;
    }

    private BigDecimal calculateTotalPrice(OrderItemDto dto) {
        if (dto.getTotalPrice() != null) {
            return dto.getTotalPrice();
        }
        if (dto.getQuantity() != null && dto.getUnitPrice() != null) {
            return dto.getUnitPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));
        }
        return BigDecimal.ZERO;
    }
}
