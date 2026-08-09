USE order_db;

CREATE TABLE IF NOT EXISTS orders (
    id              VARCHAR(255) NOT NULL PRIMARY KEY,
    customer_id     VARCHAR(255) NOT NULL,
    restaurant_id   VARCHAR(255) NOT NULL,
    status          VARCHAR(32)  NOT NULL DEFAULT 'PENDING',
    total_cents     INT UNSIGNED NOT NULL,
    eta_minutes     INT          NULL,
    delivery_lat    DECIMAL(9,6) NOT NULL,
    delivery_lng    DECIMAL(9,6) NOT NULL,
    delivery_street VARCHAR(255) NOT NULL,
    delivery_city   VARCHAR(255) NOT NULL,
    created_at      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    INDEX idx_customer_id   (customer_id),
    INDEX idx_restaurant_id (restaurant_id),
    INDEX idx_status        (status)
);

CREATE TABLE IF NOT EXISTS order_items (
    id               VARCHAR(255) NOT NULL PRIMARY KEY,
    order_id         VARCHAR(255) NOT NULL,
    menu_item_id     VARCHAR(255) NOT NULL,
    quantity         INT NOT NULL,
    unit_price_cents INT UNSIGNED NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id)
);