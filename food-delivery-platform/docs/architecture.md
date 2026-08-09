# Architecture

## System Overview

```
Customer App
     │  POST /orders
     ▼
order-service (8082)
     │  publishes → order-created
     ▼
Kafka
     ├──▶ restaurant-service (8081)   consumes order-created  → publishes order-accepted
     ├──▶ restaurant-service (8081)   consumes order-created  → publishes order-accepted
     └──▶ delivery-service (8083)     consumes order-accepted  → tracks delivery

delivery-service (8083)
     │  consumes order-accepted
     │  tracks live delivery state
     └──▶ order-service     updates status → DELIVERED
```

## Services

| Service             | Stack              | Port | DB              | Owns                            |
|---------------------|--------------------|------|-----------------|---------------------------------|
| restaurant-service  | Java / Spring Boot | 8081 | restaurant_db   | Menu, restaurant availability   |
| order-service       | Java / Spring Boot | 8082 | order_db        | Order lifecycle                 |
| delivery-service    | Java / Spring Boot | 8083 | delivery_db     | Delivery state, driver tracking |
| eta-service         | Python / FastAPI   | 8084 | —               | ETA computation                 |

## Kafka Topics (Phase 1)

Full JSON Schema definitions in `docs/kafka-contracts/`.

| Topic          | Schema file                      | Producer            | Consumers                                    |
|----------------|----------------------------------|---------------------|----------------------------------------------|
| order-created  | order-created.schema.json        | order-service       | restaurant-service |
| order-accepted | order-accepted.schema.json       | restaurant-service  | order-service, eta-service |
| order-delivered| order-delivered.schema.json      | delivery-service    | order-service |

## REST API Contracts (OpenAPI)

Full specs in `docs/openapi/`.

| Service            | Spec file                  | Key endpoints                                    |
|--------------------|----------------------------|--------------------------------------------------|
| order-service      | order-service.yaml         | POST /orders, GET /orders/{id}                   |
| restaurant-service | restaurant-service.yaml    | GET /restaurants/{id}, GET /restaurants/{id}/menu|
| delivery-service   | delivery-service.yaml      | GET /deliveries/{orderId}, PUT /drivers/{id}/location |

All services expose `GET /health` and `GET /metrics-lite` with a consistent shape.

## Database Ownership

| Service            | Database       | Key tables                                    |
|--------------------|----------------|-----------------------------------------------|
| order-service      | order_db       | orders, order_items                           |
| restaurant-service | restaurant_db  | restaurants, menu_items                       |
| delivery-service   | delivery_db    | drivers, deliveries                           |

Schema init scripts run in filename order from `infra/mysql/init/`.

## Data Store Decisions

- MySQL 8 with spatial extensions for driver location queries (see `decisions.md` ADR-001).
- `driver_locations.location` is a `POINT SRID 4326` column with a `SPATIAL INDEX` —
  Phase 6 nearest-driver query uses `ST_Distance_Sphere`.
- No Redis — spatial queries handled in MySQL to reduce operational surface area at this stage.
- Single MySQL instance with per-service logical databases; migrate to per-service RDS instances on AWS.
