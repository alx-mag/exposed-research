#!/usr/bin/env sh

docker-compose run --rm k6 run /k6-scripts/get-test.js -e TYPE=jpa -e USERS=20
