#!/usr/bin/env sh

docker compose run --rm k6 run /k6-scripts/get-test-local.js -e USERS=20 -e HOST=host.docker.internal -e PORT=9082