# food-delivery-platform
### By- Anshul, Ansh, Abhivesh, Saloni & Coco

## Structure

```
food-delivery-platform/
├── services/
│   ├── restaurant-service/      (Java/Spring Boot — port 8081)
│   ├── order-service/           (Java/Spring Boot — port 8082)
│   ├── delivery-service/        (Java/Spring Boot — port 8083)
│   └── eta-service/             (Python/FastAPI   — port 8084)
├── infra/
│   ├── docker-compose.yml
│   ├── mysql/
│   │   └── init/                (01_create_databases, 02_restaurant, 03_order, 04_delivery)
│   └── logging/
│       ├── logback-json.xml     (shared JSON logging for Java services)
│       └── logging.json         (shared JSON logging for Python services)
├── .github/
│   └── workflows/
│       └── ci.yml
├── docs/
│   ├── architecture.md
│   ├── decisions.md
│   └── kafka-contracts/
│       └── order-placed.md
└── README.md
```

## Services

| Service            | Stack       | Port | Database     |
|--------------------|-------------|------|--------------|
| restaurant-service | Spring Boot | 8081 | restaurantdb |
| order-service      | Spring Boot | 8082 | orderdb      |
| delivery-service   | Spring Boot | 8083 | deliverydb   |
| eta-service        | FastAPI     | 8084 | —            |

## Running Locally

```bash
cd infra
docker compose up --build
```

MySQL init scripts in `infra/mysql/init/` run in numbered order, creating all databases and schemas.
Java services start only after MySQL is healthy.

## Logging

- Java services: copy `infra/logging/logback-json.xml` into `src/main/resources/` and set `logging.config=classpath:logback-json.xml`. Requires `logstash-logback-encoder` dependency.
- Python services: load `infra/logging/logging.json` at startup via `logging.config.dictConfig`.

## Docs

- [Architecture](docs/architecture.md)
- [Decisions](docs/decisions.md)
- [Kafka Contracts](docs/kafka-contracts/)

## Health Checks

- Spring Boot: `GET /actuator/health`
- FastAPI: `GET /health`
