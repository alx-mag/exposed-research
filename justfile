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
  url="http://localhost:3001/d/g6zdg6/k6-load-testing-results-copy?orgId=1&from=$start&to=$end&timezone=browser&var-Measurement=\$__all"; \
  mkdir -p logs; \
  printf '"%s","%s","%s"\n' "{{name}}" "$start" "$url" >> logs/k6-tests.csv; \
  echo Results:; \
  echo "$url"

_wait t="10s":
  sleep {{t}}
