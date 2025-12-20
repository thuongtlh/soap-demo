package com.demo.inventory.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for inventory management operations.
 */
@Slf4j
@Service
public class InventoryService {

    // Simulated inventory database
    private final Map<String, InventoryItem> inventory = new HashMap<>();
    private final Map<String, Map<String, Integer>> reservations = new HashMap<>();

    public InventoryService() {
        // Initialize sample inventory
        inventory.put("PROD-001", new InventoryItem("PROD-001", "Wireless Headphones", 100, 0, "WH-A1", new BigDecimal("49.99")));
        inventory.put("PROD-002", new InventoryItem("PROD-002", "Phone Case", 250, 0, "WH-B2", new BigDecimal("19.99")));
        inventory.put("PROD-003", new InventoryItem("PROD-003", "USB Cable", 500, 0, "WH-C3", new BigDecimal("9.99")));
        inventory.put("PROD-004", new InventoryItem("PROD-004", "Laptop Stand", 25, 0, "WH-A1", new BigDecimal("79.99")));
        inventory.put("PROD-005", new InventoryItem("PROD-005", "Webcam", 5, 0, "WH-B2", new BigDecimal("89.99")));
    }

    public List<InventoryItem> checkInventory(List<String> productIds) {
        log.info("Checking inventory for products: {}", productIds);
        return productIds.stream()
                .map(id -> inventory.getOrDefault(id, createNotFoundItem(id)))
                .toList();
    }

    public ReservationResult reserveInventory(String orderId, List<ReservationRequest> items) {
        log.info("Reserving inventory for order: {}", orderId);

        String reservationId = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Map<String, Integer> orderReservations = new HashMap<>();
        boolean allReserved = true;

        List<ItemReservationResult> results = items.stream()
                .map(item -> {
                    InventoryItem inv = inventory.get(item.productId());
                    if (inv == null) {
                        return new ItemReservationResult(item.productId(), item.quantity(), 0, "OUT_OF_STOCK", "Product not found");
                    }

                    int available = inv.availableQuantity() - inv.reservedQuantity();
                    int toReserve = Math.min(available, item.quantity());

                    if (toReserve > 0) {
                        // Update reservation
                        InventoryItem updated = new InventoryItem(
                                inv.productId(), inv.productName(),
                                inv.availableQuantity(), inv.reservedQuantity() + toReserve,
                                inv.warehouseLocation(), inv.unitPrice()
                        );
                        inventory.put(inv.productId(), updated);
                        orderReservations.put(item.productId(), toReserve);
                    }

                    String status = toReserve == item.quantity() ? "RESERVED" :
                                   toReserve > 0 ? "LOW_STOCK" : "OUT_OF_STOCK";
                    String message = toReserve == item.quantity() ? "Fully reserved" :
                                    toReserve > 0 ? "Partially reserved" : "No stock available";

                    return new ItemReservationResult(item.productId(), item.quantity(), toReserve, status, message);
                })
                .toList();

        reservations.put(reservationId, orderReservations);

        allReserved = results.stream().allMatch(r -> r.reservedQuantity() == r.requestedQuantity());

        return new ReservationResult(reservationId, orderId, results, allReserved);
    }

    private InventoryItem createNotFoundItem(String productId) {
        return new InventoryItem(productId, "Unknown", 0, 0, "N/A", BigDecimal.ZERO);
    }

    // Record classes for internal use
    public record InventoryItem(String productId, String productName, int availableQuantity,
                                int reservedQuantity, String warehouseLocation, BigDecimal unitPrice) {}

    public record ReservationRequest(String productId, int quantity) {}

    public record ItemReservationResult(String productId, int requestedQuantity, int reservedQuantity,
                                        String status, String message) {}

    public record ReservationResult(String reservationId, String orderId,
                                    List<ItemReservationResult> results, boolean allReserved) {}
}
