package com.fooddelivery.orderservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class OrderControllerIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("order_db")
            .withUsername("root")
            .withPassword("root");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        if (!isDockerAvailable()) {
            return;
        }

        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    OrderRepository orderRepository;

    @Test
    void postOrder_persistsRow() throws Exception {
        var body = Map.of(
                "customerId", "cust-abc",
                "restaurantId", "rest-xyz",
                "items", List.of(
                        Map.of(
                                "menuItemId", "item-1",
                                "quantity", 2,
                                "unitPriceCents", 1500
                        )
                ),
                "deliveryAddress", Map.of(
                        "lat", 37.77,
                        "lng", -122.41,
                        "street", "1 Market St",
                        "city", "San Francisco"
                )
        );

        var result = mockMvc.perform(
                        post("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body))
                )
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalCents").value(3000))
                .andReturn();

        String orderId = objectMapper
                .readTree(result.getResponse().getContentAsString())
                .get("orderId")
                .asText();

        assertThat(orderRepository.findById(orderId)).isPresent();
    }

    @Test
    void getOrder_returns404ForUnknownId() throws Exception {
        mockMvc.perform(get("/orders/does-not-exist"))
                .andExpect(status().isNotFound());
    }

    @Test
    void postOrder_returns400ForMissingFields() throws Exception {
        mockMvc.perform(
                        post("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isBadRequest());
    }

    private static boolean isDockerAvailable() {
        try {
            DockerClientFactory.instance().client();
            return true;
        } catch (Throwable t) {
            return false;
        }
    }
}