#!/usr/bin/env just --justfile

compose_file := 'deployment/docker-compose.yaml'
spring_jpa_base_url := 'http://nginx:4000/spring-jpa'
spring_exposed_base_url := 'http://nginx:4000/spring-exposed'

alias tb := k6-test-both

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

run-k6 script base_url:
  cd k6-testing && MSYS_NO_PATHCONV=1 docker compose run --rm \
  -e USERS=20 \
  -e SLEEP_MS=0 \
  -e BASE_URL={{base_url}} \
  k6 run /k6-scripts/{{script}}

k6-test test base_url: prepare-db
  just run-k6 {{test}}.js {{base_url}}

k6-test-both script:
  start="$(date -u +"%Y-%m-%dT%H:%M:%S.%3NZ")"; \
  just k6-test {{script}} {{spring_exposed_base_url}}; \
  just _wait; \
  just k6-test {{script}} {{spring_jpa_base_url}}; \
  end="$(date -u +"%Y-%m-%dT%H:%M:%S.%3NZ")"; \
  url="http://localhost:3001/d/g6zdg6/k6-load-testing-results-copy?orgId=1&from=$start&to=$end&timezone=browser&var-Measurement=\$__all"; \
  mkdir -p logs; \
  printf '"%s","%s","%s"\n' "{{script}}" "$start" "$url" >> logs/k6-tests.csv; \
  echo Results:; \
  echo "$url"

_wait t="10":
  sleep {{t}}
