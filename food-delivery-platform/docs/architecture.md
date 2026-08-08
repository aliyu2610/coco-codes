# Architecture

## Overview

Food Delivery Platform is a microservices system composed of 4 services communicating over REST and Kafka.

## Services

| Service              | Stack         | Port | DB           |
|----------------------|---------------|------|--------------|
| restaurant-service   | Spring Boot   | 8081 | restaurantdb |
| order-service        | Spring Boot   | 8082 | orderdb      |
| delivery-service     | Spring Boot   | 8083 | deliverydb   |
| eta-service          | FastAPI       | 8084 | —            |

## Request Flow

```
Client
  └─► order-service       (creates order, publishes order.placed)
        └─► delivery-service  (consumes order.placed, creates delivery record)
                    └─► eta-service  (computes ETA, returns to client)
```

## Kafka Topics

See `kafka-contracts/` for full event schemas.

| Topic              | Producer           | Consumer(s)                        |
|--------------------|--------------------|------------------------------------|
| order.placed       | order-service    | delivery-service             |
| delivery.completed | delivery-service | order-service |
