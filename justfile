#!/usr/bin/env just --justfile

compose_file := 'deployment/docker-compose.yaml'

default:
  @just -l

users:
  @just k6-test GetUsers

rich-users:
  @just k6-test GetRichUsers

users-filtering:
  @just k6-test GetUsersFiltering

load:
  @just k6-test Load

#Restrart service in docker compose
rs service:
  docker compose -f {{compose_file}} restart {{service}}

#Recreate service in docker compose
rc service:
  docker compose -f {{compose_file}} up -d --force-recreate {{service}}

rmup service:
  docker compose -f {{compose_file}} stop {{service}} || true
  docker compose -f {{compose_file}} rm -fsv {{service}}
  docker compose -f {{compose_file}} up -d --force-recreate {{service}}

dep-exp:
  ./gradlew :spring-exposed:bootBuildImage
  docker compose -f {{compose_file}} up -d --force-recreate --no-deps spring-exposed

dep-jpa:
  ./gradlew :spring-jpa:bootBuildImage
  docker compose -f {{compose_file}} up -d --force-recreate --no-deps spring-jpa

# Execute db/clear-data.sql against the running db-service PostgreSQL container.
prepare-db:
  cat db/clear-data.sql | docker exec -i db-service psql -U postgres -d postgres

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
