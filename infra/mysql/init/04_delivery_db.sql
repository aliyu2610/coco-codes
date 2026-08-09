USE delivery_db;

CREATE TABLE IF NOT EXISTS drivers (
    id         VARCHAR(255)    NOT NULL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    is_active  TINYINT(1)  NOT NULL DEFAULT 1,
    created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS deliveries (
    id           VARCHAR(255)    NOT NULL PRIMARY KEY,
    order_id     VARCHAR(255)    NOT NULL UNIQUE,
    driver_id    VARCHAR(255)    NOT NULL,
    status       ENUM('ASSIGNED','PICKED_UP','IN_TRANSIT','DELIVERED') NOT NULL DEFAULT 'ASSIGNED',
                              -- ASSIGNED | PICKED_UP | IN_TRANSIT | DELIVERED
    eta_minutes  INT          NULL,
    delivered_at DATETIME    NULL,
    created_at   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_driver (driver_id)
);
