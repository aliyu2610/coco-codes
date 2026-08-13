package com.fooddelivery.orderservice.eta;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;

@Component
public class EtaClient {

    private final RestClient restClient;
    private final String etaServiceUrl;

    public EtaClient(
            RestClient restClient,
            @Value("${eta.service.url:http://localhost:8084}") String etaServiceUrl) {
        this.restClient = restClient;
        this.etaServiceUrl = etaServiceUrl;
    }

    public int predictEta(
            String orderId,
            double distanceKm,
            int prepTimeMinutes,
            double driverAvailability,
            double trafficFactor) {

        EtaRequest request = new EtaRequest(
                distanceKm,
                prepTimeMinutes,
                driverAvailability,
                trafficFactor,
                orderId
        );

        EtaResponse response = restClient.post()
                .uri(etaServiceUrl + "/predict-eta")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(EtaResponse.class);

        if (response == null) {
            throw new IllegalStateException("ETA service returned empty response");
        }

        return response.estimated_delivery_minutes();
    }
}