# For local de-contenerized testing

```shell
k6 run ./js/<test-name>.js -e USERS=20 -e PORT=8080
```

You can also run k6 from the root Gradle project, which makes the tasks available in the IntelliJ Gradle tool window:

```shell
./gradlew k6Run -Pscript=get-test.js -Ptype=jpa -Pusers=20
./gradlew k6Run -Pscript=get-filtering-test.js -Ptype=exposed -Pusers=20
./gradlew k6RunLocal -Pscript=get-test-local.js -Pusers=20 -Pport=9082
```

There are also predefined shortcuts:

```shell
./gradlew k6JpaGetUsers
./gradlew k6JpaFiltering
./gradlew k6ExposedGetUsers
./gradlew k6ExposedFiltering
```

# For container test run with:

```shell
docker-compose run --rm k6 run /k6-scripts/<test-name> -e TYPE=jdbc -e USERS=20
```

# For container test run against an app started on the host machine:

Inside Docker, `localhost` points to the container, not to your machine. Use
`host.docker.internal` as the host name on Docker Desktop.

```shell
docker compose run --rm k6 run /k6-scripts/get-test-local.js -e USERS=20 -e HOST=host.docker.internal -e PORT=9082
```

The `*-local.js` scripts also accept a full base URL:

```shell
docker compose run --rm k6 run /k6-scripts/get-test-local.js -e USERS=20 -e BASE_URL=http://host.docker.internal:9082/
```

Before each test you should run the following script to make every load test independent of the previous executions.

```sql
delete from books b where b.book_id <> 1;
delete from orders ;
```

Datas will be available on the grafana dashboard reachable
at: http://localhost:3000/d/k6/k6-load-testing-results?orgId=1&refresh=5s
