#!/usr/bin/env bash

set -euo pipefail

commands=(
  "just tb get-users"
  "just tb get-users-sql"
  "just tb get-users-filtering"
  "just tb get-users-rich"
  "just tb get-users-rich-sql"
  "just tb load-test"
)

interval_seconds=20
duration_seconds=$((60 * 60 * 2))
end_time=$((SECONDS + duration_seconds))
index=0

while (( SECONDS < end_time )); do
  command="${commands[index]}"
  echo "[$(date -u +"%Y-%m-%dT%H:%M:%SZ")] Running: ${command}"
  eval "${command}"

  index=$(((index + 1) % ${#commands[@]}))

  if (( SECONDS + interval_seconds >= end_time )); then
    break
  fi

  sleep "${interval_seconds}"
done
