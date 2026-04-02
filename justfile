#!/usr/bin/env just --justfile

compose_file := 'deployment/docker-compose.yaml'
spring_jpa_base_url := 'http://nginx:4000/spring-jpa'
spring_exposed_base_url := 'http://nginx:4000/spring-exposed'

alias tb := k6-test-both

alias d-e := deploy-exposed
alias d-j := deploy-jpa
alias d-b := deploy-both

default:
  @just -l

#Restrart service in docker compose
[group("deploy")]
rs service:
  docker compose -f {{compose_file}} restart {{service}}

#Recreate service in docker compose
[group("deploy")]
rc service:
  docker compose -f {{compose_file}} up -d --force-recreate {{service}}

#Delete then start service
[group("deploy")]
rmup service:
  docker compose -f {{compose_file}} stop {{service}} || true
  docker compose -f {{compose_file}} rm -fsv {{service}}
  docker compose -f {{compose_file}} up -d --force-recreate {{service}}

#Build image for `spring-exposed` project then recreate the service in docker compose
[group("deploy")]
deploy-exposed:
  ./gradlew :spring-exposed:bootBuildImage
  docker compose -f {{compose_file}} up -d --force-recreate --no-deps spring-exposed

#Build image for `spring-jpa` project then recreate the service in docker compose
[group("deploy")]
deploy-jpa:
  ./gradlew :spring-jpa:bootBuildImage
  docker compose -f {{compose_file}} up -d --force-recreate --no-deps spring-jpa

#Build image for both `spring-exposed` and `spring-jpa` projects then recreate the services in docker compose
[group("deploy")]
deploy-both:
  @just deploy-exposed
  @just deploy-jpa


#Execute db/clear-data.sql against the running db-service PostgreSQL container.
[group("k6")]
prepare-db:
  cat db/clear-data.sql | docker exec -i db-service psql -U postgres -d postgres

#Run a `test` (script name without `.js`) in `k6-scripts/` directory against the `base_url`
[group("k6")]
run-k6 test base_url:
  cd k6-testing && MSYS_NO_PATHCONV=1 docker compose run --rm \
  -e USERS=20 \
  -e SLEEP_MS=0 \
  -e BASE_URL={{base_url}} \
  k6 run /k6-scripts/{{test}}

#Run a `test` in `k6-scripts/` directory against the `base_url`
[group("k6")]
k6-test test base_url: prepare-db
  just run-k6 {{test}}.js {{base_url}}

#Run a `test` in `k6-scripts/` directory against the both `spring-exposed` and `spring-jpa`
[group("k6")]
k6-test-both test:
  start="$(date -u +"%Y-%m-%dT%H:%M:%S.%3NZ")"; \
  just k6-test {{test}} {{spring_exposed_base_url}}; \
  just _wait; \
  just k6-test {{test}} {{spring_jpa_base_url}}; \
  end="$(date -u +"%Y-%m-%dT%H:%M:%S.%3NZ")"; \
  url="http://localhost:3001/d/g6zdg6/k6-load-testing-results-copy?orgId=1&from=$start&to=$end&timezone=browser&var-Measurement=\$__all"; \
  mkdir -p logs; \
  printf '"%s","%s","%s"\n' "{{test}}" "$start" "$url" >> logs/k6-tests.csv; \
  echo Results for '{{test}}':; \
  echo "$url"

[private]
_wait t="10":
  sleep {{t}}
