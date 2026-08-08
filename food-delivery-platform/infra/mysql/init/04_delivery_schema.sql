USE deliverydb;

CREATE TABLE IF NOT EXISTS delivery (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id     BIGINT NOT NULL,
    agent_id     BIGINT,
    status       VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    assigned_at  TIMESTAMP,
    delivered_at TIMESTAMP,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
