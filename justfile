#!/usr/bin/env just --justfile

default:
  @just -l

users:
  @just k6-test GetUsers

users-filtering:
  @just k6-test GetUsersFiltering

load:
  @just k6-test Load

#Restrart service in docker compose
rs service:
  docker compose -f deployment/docker-compose.yaml restart {{service}}

k6-test name *args:
  start="$(date -u +"%Y-%m-%dT%H:%M:%S.%3NZ")"; \
  ./gradlew :spring-exposed:k6-{{name}} {{args}}; \
  just _wait; \
  ./gradlew :spring-jpa:k6-{{name}} {{args}}; \
  end="$(date -u +"%Y-%m-%dT%H:%M:%S.%3NZ")"; \
  echo Results:; \
  just _dahboard-url "$start" "$end"

_wait t="10s":
  sleep {{t}}

_dahboard-url from to:
  @echo "http://localhost:3001/d/g6zdg6/k6-load-testing-results-copy?orgId=1&from={{from}}&to={{to}}&timezone=browser&var-Measurement=\$__all"