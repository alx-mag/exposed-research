#!/usr/bin/env just --justfile

users:
  ./gradlew :spring-exposed:k6-GetUsers
  just wait
  ./gradlew :spring-jpa:k6-GetUsers

wait:
  echo 'Wait for 10s...'
  sleep 10s
  echo 'Continue'

#Restrart service in docker compose
rs service:
  docker compose -f deployment/docker-compose.yaml restart {{service}}
