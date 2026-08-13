package com.fooddelivery.orderservice.eta;

public record EtaResponse(
        int estimated_delivery_minutes
) {}