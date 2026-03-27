#!/usr/bin/env just --justfile

default:
  @just -l

users:
  ./gradlew :spring-exposed:k6-GetUsers
  just _wait
  ./gradlew :spring-jpa:k6-GetUsers

_wait:
  echo 'Wait for 10s...'
  sleep 10s
  echo 'Continue'

#Restrart service in docker compose
rs service:
  docker compose -f deployment/docker-compose.yaml restart {{service}}
