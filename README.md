# Exposed Research

This project is for research and comparison of two Spring-based implementations of the same domain:

- `spring-exposed`: Spring application built with JetBrains Exposed
- `spring-jpa`: Spring application built with Spring Data JPA

The repository also contains supporting infrastructure for load testing and metrics collection.

## Services

The Docker stack is defined in [deployment/docker-compose.yaml](deployment/docker-compose.yaml) and includes:

- `db-service`: PostgreSQL database
- `spring-exposed`: application exposed on `http://localhost:8081`
- `spring-jpa`: application exposed on `http://localhost:8082`
- `nginx`: reverse proxy on `http://localhost:4000`
- `prometheus`: metrics scraping on `http://localhost:9090`
- `influxdb`: stores k6 results on `http://localhost:8086`
- `grafana`: dashboards on `http://localhost:3001`

Useful proxied URLs through nginx:

- `http://localhost:4000/spring-exposed`
- `http://localhost:4000/spring-jpa`

## Start With Docker

Start the full environment:

```bash
docker compose -f deployment/docker-compose.yaml up -d
```

Stop it:

```bash
docker compose -f deployment/docker-compose.yaml down
```

If you change one of the Spring applications and want to rebuild and redeploy only that service, use the `just` commands described below.

## Run Spring Apps Locally

Start the database and supporting services first if your local apps depend on them:

```bash
docker compose -f deployment/docker-compose.yaml up -d db-service prometheus influxdb grafana nginx
```

Run `spring-exposed` locally:

```bash
./gradlew :spring-exposed:bootRun
```

Run `spring-jpa` locally:

```bash
./gradlew :spring-jpa:bootRun
```

If you want to run both applications locally at the same time, start them in separate terminals.

## Just Commands
 
[`just`](https://github.com/casey/just) is a command runner, similar to `make` but focused on simple project commands. This repository uses the [justfile](justfile) to define shortcuts for deployment and k6 testing.

Install `just` using the instructions from the official [installation guide](https://github.com/casey/just?tab=readme-ov-file#installation), then run commands from the repository root:

List available commands:

```bash
just -l
```

### Deploy Commands

- `just deploy-exposed`: build the `spring-exposed` image and recreate that Docker service
- `just deploy-jpa`: build the `spring-jpa` image and recreate that Docker service
- `just deploy-both`: rebuild and redeploy both Spring services
- `just d-e`: alias for `just deploy-exposed`
- `just d-j`: alias for `just deploy-jpa`
- `just d-b`: alias for `just deploy-both`
- `just rs <service>`: restart a Docker Compose service
- `just rc <service>`: recreate a Docker Compose service
- `just rmup <service>`: stop, remove, and recreate a Docker Compose service

### k6 Commands

- `just prepare-db`: execute `db/clear-data.sql` against the running PostgreSQL container
- `just run-k6 <test> <base_url>`: run a k6 script from `k6-testing`
- `just k6-test <test> <base_url>`: reset DB and then run a k6 test
- `just k6-test-both <test>`: run the same k6 test against both apps and print a Grafana link
- `just tb <test>`: alias for `just k6-test-both <test>`

Example:

```bash
just k6-test-both get-users
```
