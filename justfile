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
  ./gradlew :spring-exposed:k6-{{name}} {{args}}
  just _wait
  ./gradlew :spring-jpa:k6-{{name}} {{args}}

_wait:
  echo 'Wait for 10s...'
  sleep 10s
  echo 'Continue'