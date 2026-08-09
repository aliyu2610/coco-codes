#!/usr/bin/env bash
# Phase 2 checkpoint — run from food-delivery-platform/infra/
# Usage: bash verify-phase2.sh
set -euo pipefail

MYSQL_CONTAINER="infra-mysql-1"

echo "=== 1. Container health ==="
docker compose ps --format "table {{.Name}}	{{.Status}}"

echo ""
echo "=== 2. MySQL healthy ==="
for i in $(seq 1 24); do
  STATUS=$(docker inspect --format='{{.State.Health.Status}}' "$MYSQL_CONTAINER" 2>/dev/null || echo "starting")
  echo "MySQL status: $STATUS (attempt $i)"
  [ "$STATUS" = "healthy" ] && break
  sleep 5
done
[ "$STATUS" = "healthy" ] || (docker compose logs mysql && exit 1)

echo "MySQL is healthy."
