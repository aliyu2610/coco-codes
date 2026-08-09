# Architecture Decision Records

---

## ADR-001 — MySQL Spatial instead of Redis Geo

**Date:** 2025-07  
**Status:** Accepted

**Context:**  
Driver location lookups (nearest available driver to an order) require geo-proximity queries. The common default is Redis with the `GEOSEARCH` command.

**Decision:**  
Use MySQL 8 spatial functions (`ST_Distance_Sphere`, `POINT` column type) instead of Redis.

**Reasons:**
- Eliminates a second stateful infrastructure component (Redis) during early development.
- MySQL spatial indexes (`SPATIAL INDEX`) are sufficient for the expected driver fleet size (<10 k active drivers per region).
- Keeps the operational footprint to one database technology; easier to run locally and in CI.

**Trade-offs:**  
If query latency becomes a bottleneck at scale, introduce Redis Geo as a read-through cache in front of MySQL without changing the write path.

**Throughput ceiling (explicit):**  
The `ST_Distance_Sphere` query over a `SPATIAL INDEX` is suitable for dozens of concurrent assignment requests and a fleet of up to ~10k active drivers per region. Beyond that, a full table scan of available drivers becomes the bottleneck. Re-architecting options at ride-share scale: a managed geospatial store (e.g. Redis Geo, PostGIS, or a dedicated geo-search service) as a read-through cache in front of MySQL, or sharding the `driver_locations` table by geographic region.

---

## ADR-002 — Single MySQL instance, per-service databases

**Date:** 2025-07  
**Status:** Accepted

**Context:**  
True microservice isolation calls for a separate database server per service. That adds operational overhead before any business logic exists.

**Decision:**  
Run one MySQL 8 container with three logical databases (`restaurant_db`, `order_db`, `delivery_db`). Each service connects only to its own database via its own credentials (to be added in a later phase).

**Trade-offs:**  
Noisy-neighbour risk on a single instance. Migrate to per-service RDS instances when deploying to AWS.

---

