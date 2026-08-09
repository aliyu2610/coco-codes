USE order_db;

CREATE TABLE IF NOT EXISTS orders (
    id            VARCHAR(255)     NOT NULL PRIMARY KEY,
    customer_id   VARCHAR(255)     NOT NULL,
    restaurant_id VARCHAR(255)     NOT NULL,
    status        ENUM('PENDING','ACCEPTED','REJECTED','DRIVER_ASSIGNED','DELIVERED') NOT NULL DEFAULT 'PENDING',
                                -- PENDING | ACCEPTED | REJECTED | DRIVER_ASSIGNED | DELIVERED
    total_cents   INT UNSIGNED NOT NULL,
    eta_minutes   INT          NULL,
    delivery_lat  DECIMAL(9,6) NOT NULL,
    delivery_lng  DECIMAL(9,6) NOT NULL,
    delivery_street VARCHAR(255) NOT NULL,
    delivery_city VARCHAR(255) NOT NULL,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_customer (customer_id),
    INDEX idx_restaurant (restaurant_id)
);

CREATE TABLE IF NOT EXISTS order_items (
    id              VARCHAR(255)     NOT NULL PRIMARY KEY,
    order_id        VARCHAR(255)     NOT NULL,
    menu_item_id    VARCHAR(255)     NOT NULL,
    quantity        INT          NOT NULL,
    unit_price_cents INT UNSIGNED NOT NULL,
    CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders(id)
);
