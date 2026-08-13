package com.fooddelivery.orderservice.eta;

public record EtaRequest(
        double distance_km,
        int prep_time_minutes,
        double driver_availability,
        double traffic_factor,
        String order_id
) {}