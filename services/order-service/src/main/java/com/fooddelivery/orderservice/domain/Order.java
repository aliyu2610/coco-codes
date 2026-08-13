package com.fooddelivery.orderservice.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    private String id;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "restaurant_id", nullable = false)
    private String restaurantId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "status", nullable = false, length = 32)
    private OrderStatus status;

    @Column(name = "total_cents", nullable = false)
    private int totalCents;

    @Column(name = "eta_minutes")
    private Integer etaMinutes;

    // Delivery coordinates
    @Column(name = "delivery_lat", nullable = false, precision = 9, scale = 6)
    private BigDecimal deliveryLat;

    @Column(name = "delivery_lng", nullable = false, precision = 9, scale = 6)
    private BigDecimal deliveryLng;

    @Column(name = "delivery_street", nullable = false)
    private String deliveryStreet;

    @Column(name = "delivery_city", nullable = false)
    private String deliveryCity;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // Items are loaded separately via OrderItemRepository
    @Transient
    private List<OrderItem> items = new ArrayList<>();

    protected Order() {
    }

    public Order(
            String id,
            String customerId,
            String restaurantId,
            int totalCents,
            BigDecimal deliveryLat,
            BigDecimal deliveryLng,
            String deliveryStreet,
            String deliveryCity
    ) {
        this.id = id;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.status = OrderStatus.PENDING;
        this.totalCents = totalCents;
        this.deliveryLat = deliveryLat;
        this.deliveryLng = deliveryLng;
        this.deliveryStreet = deliveryStreet;
        this.deliveryCity = deliveryCity;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void accept() {
        transition(OrderStatus.ACCEPTED);
    }

    /**
     * Sets the ETA without changing the order status.
     * ETA is calculated when the order is created.
     */
    public void setEtaMinutes(Integer etaMinutes) {
        this.etaMinutes = etaMinutes;
        this.updatedAt = Instant.now();
    }

    /**
     * Assigns the driver using the ETA that was already calculated.
     * This method does NOT call the ETA service.
     */
    public void assignDriver(int etaMinutes) {
        this.etaMinutes = etaMinutes;
        transition(OrderStatus.DRIVER_ASSIGNED);
    }

    public void markDelivered() {
        transition(OrderStatus.DELIVERED);
    }

    private void transition(OrderStatus next) {
        this.status = next;
        this.updatedAt = Instant.now();
    }

    public void setItems(List<OrderItem> items) {
        this.items = new ArrayList<>(items);
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public int getTotalCents() {
        return totalCents;
    }

    public Integer getEtaMinutes() {
        return etaMinutes;
    }

    public BigDecimal getDeliveryLat() {
        return deliveryLat;
    }

    public BigDecimal getDeliveryLng() {
        return deliveryLng;
    }

    public String getDeliveryStreet() {
        return deliveryStreet;
    }

    public String getDeliveryCity() {
        return deliveryCity;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}