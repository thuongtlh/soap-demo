package com.demo.rest.client;

import com.demo.rest.generated.CreateOrderRequest;
import com.demo.rest.generated.CreateOrderResponse;
import com.demo.rest.generated.GetOrderRequest;
import com.demo.rest.generated.GetOrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * SOAP Client for communicating with the Order SOAP Service.
 *
 * This client uses Spring's WebServiceTemplate to make SOAP calls.
 * It abstracts the SOAP communication details from the service layer.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SoapOrderClient {

    private final WebServiceTemplate webServiceTemplate;

    /**
     * Send a CreateOrder request to the SOAP service.
     *
     * The WebServiceTemplate:
     * 1. Marshals the request object to XML
     * 2. Wraps it in a SOAP envelope
     * 3. Sends it to the SOAP service
     * 4. Receives the SOAP response
     * 5. Unmarshals the response XML to Java object
     *
     * @param request The SOAP CreateOrderRequest
     * @return The SOAP CreateOrderResponse
     */
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        log.info("Sending CreateOrder SOAP request for customer: {}",
                request.getCustomer().getCustomerId());

        CreateOrderResponse response = (CreateOrderResponse) webServiceTemplate
                .marshalSendAndReceive(request);

        log.info("Received CreateOrder SOAP response with orderId: {}",
                response.getOrderId());

        return response;
    }

    /**
     * Send a GetOrder request to the SOAP service.
     *
     * @param orderId The order ID to retrieve
     * @return The SOAP GetOrderResponse
     */
    public GetOrderResponse getOrder(String orderId) {
        log.info("Sending GetOrder SOAP request for orderId: {}", orderId);

        GetOrderRequest request = new GetOrderRequest();
        request.setOrderId(orderId);

        GetOrderResponse response = (GetOrderResponse) webServiceTemplate
                .marshalSendAndReceive(request);

        log.info("Received GetOrder SOAP response for orderId: {}", orderId);

        return response;
    }
}
