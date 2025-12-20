package com.demo.inventory.endpoint;

import com.demo.inventory.config.WebServiceConfig;
import com.demo.inventory.generated.*;
import com.demo.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * SOAP Endpoint for Inventory operations.
 */
@Slf4j
@Endpoint
@RequiredArgsConstructor
public class InventoryEndpoint {

    private final InventoryService inventoryService;

    @PayloadRoot(namespace = WebServiceConfig.NAMESPACE_URI, localPart = "CheckInventoryRequest")
    @ResponsePayload
    public CheckInventoryResponse checkInventory(@RequestPayload CheckInventoryRequest request) {
        log.info("Received CheckInventoryRequest for {} products", request.getProductIds().size());

        List<InventoryService.InventoryItem> items = inventoryService.checkInventory(request.getProductIds());

        CheckInventoryResponse response = new CheckInventoryResponse();
        items.forEach(item -> {
            InventoryItemType itemType = new InventoryItemType();
            itemType.setProductId(item.productId());
            itemType.setProductName(item.productName());
            itemType.setAvailableQuantity(item.availableQuantity());
            itemType.setReservedQuantity(item.reservedQuantity());
            itemType.setWarehouseLocation(item.warehouseLocation());
            itemType.setUnitPrice(item.unitPrice());
            response.getItems().add(itemType);
        });
        response.setCheckedAt(toXmlGregorianCalendar(LocalDateTime.now()));

        log.info("Returning CheckInventoryResponse with {} items", response.getItems().size());
        return response;
    }

    @PayloadRoot(namespace = WebServiceConfig.NAMESPACE_URI, localPart = "ReserveInventoryRequest")
    @ResponsePayload
    public ReserveInventoryResponse reserveInventory(@RequestPayload ReserveInventoryRequest request) {
        log.info("Received ReserveInventoryRequest for order: {}", request.getOrderId());

        List<InventoryService.ReservationRequest> reservationRequests = request.getItems().stream()
                .map(item -> new InventoryService.ReservationRequest(item.getProductId(), item.getRequestedQuantity()))
                .toList();

        InventoryService.ReservationResult result = inventoryService.reserveInventory(
                request.getOrderId(), reservationRequests);

        ReserveInventoryResponse response = new ReserveInventoryResponse();
        response.setReservationId(result.reservationId());
        response.setOrderId(result.orderId());
        response.setAllReserved(result.allReserved());
        response.setReservedAt(toXmlGregorianCalendar(LocalDateTime.now()));

        result.results().forEach(r -> {
            ReservationResultType resultType = new ReservationResultType();
            resultType.setProductId(r.productId());
            resultType.setRequestedQuantity(r.requestedQuantity());
            resultType.setReservedQuantity(r.reservedQuantity());
            resultType.setStatus(InventoryStatusType.fromValue(r.status()));
            resultType.setMessage(r.message());
            response.getResults().add(resultType);
        });

        log.info("Returning ReserveInventoryResponse: reservationId={}, allReserved={}",
                response.getReservationId(), response.isAllReserved());
        return response;
    }

    private XMLGregorianCalendar toXmlGregorianCalendar(LocalDateTime dateTime) {
        try {
            GregorianCalendar gc = GregorianCalendar.from(dateTime.atZone(ZoneId.systemDefault()));
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        } catch (Exception e) {
            log.error("Error converting date", e);
            return null;
        }
    }
}
