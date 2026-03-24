#!/usr/bin/env sh
set -eu

SCRIPT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
REPO_ROOT="$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)"

cd "$REPO_ROOT"

./gradlew :spring-jpa:bootBuildImage

docker compose -f deployment/docker-compose.yaml up -d --force-recreate --no-deps spring-jpa
