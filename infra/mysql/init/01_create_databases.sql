-- Runs automatically on first MySQL container start via /docker-entrypoint-initdb.d
-- Only the three required databases are created for the simplified stack.

CREATE DATABASE IF NOT EXISTS restaurant_db;
CREATE DATABASE IF NOT EXISTS order_db;
CREATE DATABASE IF NOT EXISTS delivery_db;
