# Architecture Decision Records

## ADR-001: Python for ETA and Assignment Services

**Status:** Accepted

**Context:** ETA calculation and agent assignment benefit from ML/geo libraries (scikit-learn, geopy) more readily available in Python.

**Decision:** Use FastAPI for eta-service and assignment-service.

**Consequences:** Two runtimes in the monorepo; shared logging config handles both via `infra/logging/`.

---

## ADR-002: Flask for ops-dashboard instead of Grafana

**Status:** Accepted

**Context:** Grafana requires a separate metrics backend (Prometheus). A lightweight Flask dashboard can aggregate service health and key metrics via internal REST calls without additional infra.

**Decision:** Build ops-dashboard as a Flask app that polls `/actuator/health` and `/health` endpoints.

**Consequences:** Custom UI work required; no out-of-the-box alerting.

---

## ADR-003: Per-service MySQL schemas

**Status:** Accepted

**Context:** Shared schema creates tight coupling between services.

**Decision:** Each Java service owns its own database (`restaurantdb`, `orderdb`, `deliverydb`). Init scripts in `infra/mysql/init/` are numbered to run in order.

**Consequences:** No cross-service JOINs; data consistency enforced via events.
