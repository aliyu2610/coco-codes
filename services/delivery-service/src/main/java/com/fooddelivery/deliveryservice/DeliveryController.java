package com.fooddelivery.deliveryservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.Map;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createDelivery(
            @RequestBody CreateDeliveryRequest request) {

        var delivery = deliveryService.createDelivery(
                request.orderId(),
                request.driverId(),
                request.etaMinutes()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "deliveryId", delivery.getId(),
                "orderId", delivery.getOrderId(),
                "driverId", delivery.getDriverId(),
                "status", delivery.getStatus().name(),
                "etaMinutes", delivery.getEtaMinutes()
        ));
    }
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable String orderId,
            @RequestBody Map<String, String> body) {

        String statusStr = body.get("status");
        if (statusStr == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "status field required"));
        }

        DeliveryStatus next;
        try {
            next = DeliveryStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "unknown status: " + statusStr));
        }

        var delivery = deliveryService.advanceStatus(orderId, next);
        return ResponseEntity.ok(Map.of(
            "orderId", delivery.getOrderId(),
            "status",  delivery.getStatus().name()
        ));
    }
}
