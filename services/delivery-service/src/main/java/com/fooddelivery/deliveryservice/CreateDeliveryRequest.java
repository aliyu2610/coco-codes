package com.fooddelivery.deliveryservice;

public record CreateDeliveryRequest(
        String orderId,
        String driverId,
        int etaMinutes
) {}