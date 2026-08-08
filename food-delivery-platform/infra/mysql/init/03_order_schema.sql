USE orderdb;

CREATE TABLE IF NOT EXISTS orders (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id BIGINT NOT NULL,
    customer_id   BIGINT NOT NULL,
    status        VARCHAR(50) NOT NULL DEFAULT 'PLACED',
    total_amount  DECIMAL(10, 2),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
