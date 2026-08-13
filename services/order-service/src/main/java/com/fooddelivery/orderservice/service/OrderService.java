package com.fooddelivery.orderservice.service;

import com.fooddelivery.orderservice.domain.Order;
import com.fooddelivery.orderservice.domain.OrderItem;
import com.fooddelivery.orderservice.repository.OrderItemRepository;
import com.fooddelivery.orderservice.repository.OrderRepository;
import com.fooddelivery.orderservice.web.CreateOrderRequest;
import com.fooddelivery.orderservice.web.OrderResponse;
import com.fooddelivery.orderservice.eta.EtaClient;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private static final Logger log =
            LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final EtaClient etaClient;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            EtaClient etaClient) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.etaClient = etaClient;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest req) {

        String orderId = UUID.randomUUID().toString();
        MDC.put("orderId", orderId);

        try {

            int totalCents = req.items().stream()
                    .mapToInt(i -> i.quantity() * i.unitPriceCents())
                    .sum();

            var order = new Order(
                    orderId,
                    req.customerId(),
                    req.restaurantId(),
                    totalCents,
                    BigDecimal.valueOf(req.deliveryAddress().lat()),
                    BigDecimal.valueOf(req.deliveryAddress().lng()),
                    req.deliveryAddress().street(),
                    req.deliveryAddress().city()
            );

            orderRepository.save(order);

            List<OrderItem> items = req.items().stream()
                    .map(i -> new OrderItem(
                            UUID.randomUUID().toString(),
                            orderId,
                            i.menuItemId(),
                            i.quantity(),
                            i.unitPriceCents()))
                    .toList();

            orderItemRepository.saveAll(items);
            order.setItems(items);

            // Call ETA service when the order is created
            int etaMinutes = etaClient.predictEta(
                    orderId,
                    5.0,
                    20,
                    0.8,
                    0.3
            );

            log.info(
                    "ETA received from eta-service: {} minutes",
                    etaMinutes
            );

            // Store ETA without changing the order status
            order.setEtaMinutes(etaMinutes);
            orderRepository.save(order);

            log.info(
                    "Order created status={} totalCents={} etaMinutes={}",
                    order.getStatus(),
                    totalCents,
                    etaMinutes
            );

            return OrderResponse.from(order, items);

        } finally {
            MDC.remove("orderId");
        }
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(String orderId) {

        MDC.put("orderId", orderId);

        try {

            Order order = findOrder(orderId);

            List<OrderItem> items =
                    orderItemRepository.findByOrderId(orderId);

            order.setItems(items);

            return OrderResponse.from(order, items);

        } finally {
            MDC.remove("orderId");
        }
    }

    @Transactional
    public void markAccepted(String orderId) {

        MDC.put("orderId", orderId);

        try {

            Order order = findOrder(orderId);

            order.accept();

            orderRepository.save(order);

            log.info("Order status updated to ACCEPTED");

        } finally {
            MDC.remove("orderId");
        }
    }

    @Transactional
    public void markDriverAssigned(String orderId) {

        MDC.put("orderId", orderId);

        try {

            Order order = findOrder(orderId);

            // ETA is already calculated during order creation.
            // Driver assignment does NOT call eta-service.

            order.assignDriver(order.getEtaMinutes());

            orderRepository.save(order);

            log.info(
                    "Order status updated to DRIVER_ASSIGNED etaMinutes={}",
                    order.getEtaMinutes()
            );

        } finally {
            MDC.remove("orderId");
        }
    }

    @Transactional
    public void markDelivered(String orderId) {

        MDC.put("orderId", orderId);

        try {

            Order order = findOrder(orderId);

            order.markDelivered();

            orderRepository.save(order);

            log.info("Order status updated to DELIVERED");

        } finally {
            MDC.remove("orderId");
        }
    }

    private Order findOrder(String orderId) {

        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Order not found: " + orderId
                        ));
    }
}