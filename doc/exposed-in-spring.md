# Exposed ORM в Spring Boot: полное руководство

## 1. Что такое Exposed

Exposed — SQL-библиотека для Kotlin, разработанная JetBrains. Предоставляет два подхода к работе с БД: типобезопасный DSL, обёртывающий SQL, и легковесный DAO-слой, реализующий паттерн ORM. Работает поверх JDBC и R2DBC (с версии 1.0). Лицензия — Apache 2.0.

Проект появился примерно в 2016 году как внутренний инструмент JetBrains. Используется внутри компании в production более 8 лет. С 2024 года JetBrains выделила отдельную команду разработчиков и технического лида. Версия 1.0 вышла в январе 2026 — первый мажорный релиз с гарантией стабильности API. На март 2026 — ~9.1k звёзд на GitHub, ~755 форков, ~3000 коммитов.

---

## 2. Ключевые особенности Exposed

### 2.1. Kotlin-first дизайн

Exposed использует property delegation, extension functions, operator overloading, лямбды с ресивером. В DSL-подходе нет аннотаций, no-arg конструкторов, обязательного наследования от фреймворковых классов.

### 2.2. Два подхода: DSL и DAO

Exposed предоставляет два API, которые можно комбинировать в одном проекте.

**DSL** — типобезопасный SQL builder. Описание таблицы:

```kotlin
object Cities : IntIdTable() {
    val name = varchar("name", 50)
}

object Users : IntIdTable() {
    val name = varchar("name", 50)
    val email = varchar("email", 100).uniqueIndex()
    val age = integer("age")
    val city = reference("city_id", Cities)
}
```

CRUD через DSL:

```kotlin
// Create
val cityId = Cities.insertAndGetId { it[name] = "Moscow" }

Users.insert {
    it[name] = "Alice"
    it[email] = "alice@example.com"
    it[age] = 30
    it[city] = cityId
}

// Read
val user = Users.selectAll()
    .where { Users.name eq "Alice" }
    .firstOrNull()

// Update
Users.update({ Users.id eq 1 }) { it[age] = 31 }

// Delete
Users.deleteWhere { Users.id eq 1 }
```

**DAO** — надстраивается поверх тех же Table-определений:

```kotlin
class City(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<City>(Cities)
    var name by Cities.name
    val users by User referrersOn Users.city
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)
    var name by Users.name
    var email by Users.email
    var age by Users.age
    var city by City referencedOn Users.city
}
```

CRUD через DAO:

```kotlin
val moscow = City.new { name = "Moscow" }

val alice = User.new {
    name = "Alice"
    email = "alice@example.com"
    age = 30
    city = moscow
}

val found = User.findById(1)
alice.age = 31          // UPDATE генерируется автоматически
alice.delete()
```

DAO подходит для простых CRUD-операций. DSL — для сложных запросов с JOIN, агрегацией, подзапросами. Оба подхода работают с одними и теми же Table-определениями.

### 2.3. Поддержка СУБД

PostgreSQL, MySQL/MariaDB, Oracle, SQL Server, H2, SQLite.

### 2.4. Модульная архитектура

Более 20 модулей. Разработчик подключает только нужные:

| Модуль | Назначение |
|--------|-----------|
| `exposed-core` | DSL API, описание таблиц, типы колонок |
| `exposed-jdbc` | Транспорт через JDBC |
| `exposed-r2dbc` | Транспорт через R2DBC (с версии 1.0) |
| `exposed-dao` | ORM-слой (только с JDBC) |
| `exposed-kotlin-datetime` | Поддержка `kotlinx.datetime` |
| `exposed-json` | Колонки `json`/`jsonb` |
| `exposed-crypt` | Шифрование колонок на стороне клиента |
| `exposed-spring-boot-starter` | Интеграция со Spring Boot 3 |
| `exposed-migration-jdbc` | Генерация diff-миграций |

### 2.5. Поддержка R2DBC

С версии 1.0 те же Table-определения и DSL-запросы (включая JOIN) работают с реактивными R2DBC-драйверами. DAO API с R2DBC не работает — доступен только DSL.

---

## 3. Настройка Exposed в Spring Boot

### 3.1. Зависимости

```kotlin
// build.gradle.kts
dependencies {
    implementation(platform("org.jetbrains.exposed:exposed-bom:1.1.1"))
    implementation("org.jetbrains.exposed:exposed-spring-boot-starter")
    // Driver implementation
    implementation("org.jetbrains.exposed:(exposed-jdbc|exposed-r2dbc)")    
    // ORM support
    implementation("org.jetbrains.exposed:exposed-dao")
}
```

Для Spring Boot 4 используется `exposed-spring-boot4-starter`.

### 3.2. application.properties

Starter переиспользует стандартные свойства Spring DataSource:

```properties
# Datasource connection
spring.datasource.url=jdbc:postgresql://localhost:5432/mydb
spring.datasource.username=postgres
spring.datasource.password=secret

# Schema auto-creation by Table classes
spring.exposed.generate-ddl=true

# SQL query loging
spring.exposed.show-sql=true
```

`spring.exposed.generate-ddl=true` — аналог `ddl-auto` в Hibernate. В GraalVM native image не работает из-за ограничений Spring AOT — нужно использовать `SchemaUtils.create()` вручную.

### 3.3. Автоконфигурация

Starter автоматически создаёт `Database` бин на основе Spring DataSource и подключает `SpringTransactionManager`, интегрирующий транзакции Exposed с `@Transactional`.

[//]: # (!В SpringBoot 4, кажется, этого не делают)
Рекомендуется исключить `DataSourceTransactionManagerAutoConfiguration`, чтобы Spring не создавал стандартный `DataSourceTransactionManager` — он конфликтует с `SpringTransactionManager` из Exposed, что может привести к тому, что транзакции управляются не тем менеджером:

```kotlin
@SpringBootApplication(exclude = [DataSourceTransactionManagerAutoConfiguration::class])
class Application
```

Кастомизация параметров Exposed — через бин `DatabaseConfig`:

```kotlin
@Bean
fun databaseConfig() = DatabaseConfig {
    sqlLogger = null // Slf4jSqlDebugLogger
    defaultIsolationLevel = -1 // driver default
    defaultRepetitionAttempts = 1
    defaultMinRepetitionDelay = 0
    defaultMaxRepetitionDelay = 0
    defaultFetchSize = null // driver default
    defaultMaxEntityCacheSize = 1000
    useNestedTransactions = false
    warnLongQueriesDuration = null
    logTooMuchResultSetsThreshold = 0
    keepLoadedReferencesOutOfTransaction = false
    explicitDialect = null // auto
}
```

---

## 4. Exposed vs Spring Data JPA: сравнение на примерах

### 4.1. Определение модели данных

В Exposed используются классы для описания таблиц. Они используются для построения SQL запросов и определения классов сущностей. Поэтому для того, чтобы создать сущность в Exposed, нужно как минимум 2 класса: для таблицы и для самой сущности. В JPA описываются только сущности, а их связь с таблицей в БД определяется через аннотации, не используя отдельных классов для таблиц.

**Spring Data JPA:**

```kotlin
@Entity
@Table(name = "cities")
class City(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,
    var name: String = ""
)
```

**Exposed DAO:**
```kotlin
// Table
object Cities : IntIdTable() {
    val name = varchar("name", 50)
}

// Entity
class City(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<City>(Cities)

    var name by Cities.name
}
```

### 4.6. Связи между сущностями

В Exposed, как и в JPA, есть возможность описывать связи между таблицами, и отражать это в сущностях.

**Spring Data JPA:**

```kotlin
@Entity
class City(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,
    var name: String = "",
    @OneToMany(mappedBy = "city")           // One-to-Many
    var users: MutableList<User> = mutableListOf()
)

@Entity
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,
    var name: String = "",
    @ManyToOne                              // Many-to-One
    var city: City? = null,
    @OneToOne(cascade = [CascadeType.ALL])  // One-to-One
    var profile: Profile? = null,
    @ManyToMany                             // Many-to-Many
    var roles: MutableList<Role> = mutableListOf()
)
```

**Exposed DAO:**

```kotlin
object Cities : IntIdTable()
object Users : IntIdTable() {
    val name = varchar("name", 50)
    val city = reference("city_id", Cities)          
    val profile = reference("profile_id", Profiles) 
}
object UserRoles : Table() {
    val user = reference("user_id", Users)
    val role = reference("role_id", Roles)
    override val primaryKey = PrimaryKey(user, role)
}

class City(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<City>(Cities)
    var name by Cities.name
    val users by User referrersOn Users.city          // One-to-Many
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)
    var name by Users.name
    var city by City referencedOn Users.city          // Many-to-One
    var profile by Profile referencedOn Users.profile // One-to-One
    var roles by Role via UserRoles                   // Many-to-Many
}
```

### 4.7. Проекции

Проблемы, которые решают проекции в Spring Data, в Exposed решаются через создание отдельной Entity.

**Spring Data JPA — интерфейсная проекция:**

```kotlin
interface UserNameOnly {
    fun getName(): String
    fun getEmail(): String
}

interface UserRepository : JpaRepository<User, Int> {
    fun findByAge(age: Int): List<UserNameOnly>
}
```

**Exposed DAO — отдельная сущность на те же колонки:**

```kotlin
class UserNameEmail(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserNameEmail>(Users)
    val name by Users.name
    val email by Users.email
}

// Использование:
UserNameEmail.find { Users.age eq 25 }.toList()
```


### 4.2. CRUD-операции

В JPA реализация CRUD генерируется автоматически через интерфейс репозитория. В Exposed — пишется вручную, но объём кода сопоставим.

**Spring Data JPA:**

```kotlin
interface UserRepository : JpaRepository<User, Int>

@Service
class UserService(private val userRepository: UserRepository) {

    fun create(name: String, email: String, age: Int, city: City): User =
        userRepository.save(User(name = name, email = email, age = age, city = city))

    fun findById(id: Int): User? =
        userRepository.findById(id).orElse(null)

    fun findAll(): List<User> =
        userRepository.findAll()

    fun update(id: Int, name: String): User? {
        val user = userRepository.findById(id).orElse(null) ?: return null
        user.name = name
        return userRepository.save(user)
    }

    fun delete(id: Int) =
        userRepository.deleteById(id)
}
```

**Exposed DAO:**

```kotlin
@Service
class UserService {

    @Transactional
    fun create(name: String, email: String, age: Int, city: City): User =
        User.new { this.name = name; this.email = email; this.age = age; this.city = city }

    @Transactional(readOnly = true)
    fun findById(id: Int): User? =
        User.findById(id)

    @Transactional(readOnly = true)
    fun findAll(): List<User> =
        User.all().toList()

    @Transactional
    fun update(id: Int, name: String): User? =
        User.findById(id)?.apply { this.name = name }

    @Transactional
    fun delete(id: Int) {
        User.findById(id)?.delete()
    }
}
```

### 4.3. Запросы с фильтрацией

В Spring Data для запросов с фильтрацией используются derived методы в репозиториях. В Exposed это делается через DAO API, предоставляемый из класса-сущности.

**Spring Data JPA — derived query methods:**

```kotlin
interface UserRepository : JpaRepository<User, Int> {
    fun findByNameAndAge(name: String, age: Int): List<User>
}
```

**Exposed DAO:**

```kotlin
@Transactional
fun findByNameAndAge(name: String, age: Int): List<User> =
    User.find { (Users.name eq name) and (Users.age eq age) }.toList()
```

### 4.4. Пагинация и сортировка

В Exposed нет встроенной абстракции `Page<T>`. Вместо этого используются запросы с `limit`, `offset`, `orderBy` параметрами.

**Spring Data JPA:**

```kotlin
@Service
class UserService(private val userRepository: UserRepository) {

    fun findPage(page: Int, size: Int): Page<User> =
        userRepository.findAll(PageRequest.of(
            page, 
            size, 
            Sort.by("name")
        )
    )
}
```

**Exposed DAO API:**

```kotlin
@Service
class UserService {

    @Transactional(readOnly = true)
    fun findPage(page: Int, size: Int): List<User> =
        User.all()
            .limit(size)
            .offset((page * size).toLong())
            .orderBy(Users.name to ASC)
            .toList()
}
```

### 4.8. Динамические запросы

Для динамических запросов (когда набор условий фильтрации определяется в runtime) derived методы в репозиториях Spring Data не подходят — требуется Criteria API. В Exposed же для динамических запросов используется тот же DSL, что и для обычных.

**Spring Data JPA — Specification API:**

```kotlin
interface UserRepository : JpaRepository<User, Int>, JpaSpecificationExecutor<User>

@Service
class UserService(private val userRepository: UserRepository) {

    fun findUsers(name: String?, minAge: Int?): List<User> {
        val spec = Specification.where<User> { root, _, cb ->
                name?.let { cb.equal(root.get<String>("name"), it) }
            }
            .and { root, _, cb ->
                minAge?.let { cb.greaterThan(root.get("age"), it) }
            }
        return userRepository.findAll(spec)
    }
}
```

В Criteria API обращение к полям — через строки (`root.get<String>("name")`), что не проверяется компилятором.

**Exposed DSL:**

```kotlin
@Service
class UserService {

    @Transactional(readOnly = true)
    fun findUsers(name: String?, minAge: Int?): List<UserDto> {
        val query = Users.selectAll()

        name?.let { query.andWhere { Users.name eq it } }
        minAge?.let { query.andWhere { Users.age greaterEq it } }

        return query.map { UserDto(it[Users.id].value, it[Users.name], it[Users.age]) }
    }
}
```

В Exposed обращение к полям — типобезопасные ссылки на колонки (`Users.name eq value`), проверяемые компилятором. Переход от обычных запросов к динамическим не требует смены API.

### 4.5. Кастомные запросы

В JPA запрос — строка JPQL, ошибки в которой обнаруживаются в runtime. В Exposed — Kotlin-код, ошибки обнаруживаются компилятором.

**Spring Data JPA:**

```kotlin
interface UserRepository : JpaRepository<User, Int> {
    @Query("SELECT u FROM User u WHERE u.age > :minAge AND u.city.name = :city")
    fun findByCityAndMinAge(
        @Param("city") city: String, 
        @Param("minAge") minAge: Int
    ): List<User>
}
```

**Exposed DSL:**

```kotlin
fun findByCityAndMinAge(city: String, minAge: Int): List<User> {
    val rows = Users.innerJoin(Cities)
        .selectAll()
        .where { (Users.age greater minAge) and (Cities.name eq city) }
    return User.wrapRows(rows).toList()
}
```




### 4.9. Миграции схемы

**Hibernate:** `ddl-auto=update` применяет изменения напрямую к БД без генерации скриптов.

**Exposed** предоставляет три уровня:

`spring.exposed.generate-ddl=true` — аналог `ddl-auto` в Hibernate. Автоматически создаёт таблицы по Table-определениям при старте приложения.

`SchemaUtils` — ручное управление схемой, для прототипов и тестов. Вызывается в startup-коде (это один из случаев, когда `transaction {}` оправдан в Spring Boot — транзакционная инфраструктура может быть ещё не готова):

```kotlin
@Component
class DatabaseInitializer(private val database: Database) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        transaction(database) {
            SchemaUtils.create(Cities, Users)
        }
    }
}
```

`exposed-migration` — генерация diff-скриптов. Сравнивает Table-определения с реальной БД и выдаёт список SQL-выражений:

```kotlin
@Component
class MigrationGenerator(private val database: Database) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        transaction(database) {
            val statements = MigrationUtils.statementsRequiredForDatabaseMigration(Cities, Users)
            // ["ALTER TABLE users ADD COLUMN email VARCHAR(100)"]

            MigrationUtils.generateMigrationScript(
                Cities, Users,
                scriptDirectory = "src/main/resources/db/migration",
                scriptName = "V2__Add_email_column"
            )
        }
    }
}
```

Для production рекомендуется связка: `MigrationUtils` генерирует SQL-скрипт → Flyway/Liquibase управляет версионированием.

### 4.10. Сводная таблица сравнения

| Задача | Spring Data JPA | Exposed |
|--------|----------------|---------|
| Типобезопасный SQL DSL | Нет встроенного. Criteria API оперирует строками для обращения к полям сущностей | Запросы проверяются компилятором без annotation processor'ов и кодогенерации |
| API для простых CRUD-операций | Через репозитории: `interface UserRepo : JpaRepository<User, Long>` — реализация генерируется автоматически | Через DAO: `User.new {}`, `User.findById()`, `User.all()`, `entity.delete()`. Через DSL: `Users.insert {}`, `selectAll()`, `update {}`, `deleteWhere {}` |
| Пагинация и сортировка | `Pageable`, `Page<T>`, `Sort` — готовая абстракция | `.limit(n).offset(m).orderBy(...)`. Встроенной абстракции `Page` нет |
| Связи (1:N, N:M) | `@OneToMany`, `@ManyToMany`, `@JoinTable` | DSL: `reference()`, `innerJoin`/`leftJoin`. DAO: `referencedOn`, `referrersOn`, `via` |
| Кастомные запросы | `@Query("SELECT ... FROM ...")` — JPQL или native SQL в строке | DSL-выражение в Kotlin-коде с проверкой типов на этапе компиляции |
| Проекции | Интерфейсные проекции, DTO-проекции через JPQL | Выбор конкретных колонок через `.select(Users.name, Users.age)` или создание отдельной Entity на ту же таблицу |
| Динамические запросы | `Specification<T>` + `JpaSpecificationExecutor`. Обращение к полям через строки; JPA Metamodel редко используется в Kotlin | `andWhere {}` или комбинирование `Op<Boolean>`. Поля — типобезопасные ссылки, проверяются компилятором |
| Миграции схемы | `ddl-auto=update` — применяет изменения напрямую, без генерации скриптов | `spring.exposed.generate-ddl=true` (аналог `ddl-auto`). `MigrationUtils` — генерация diff-скриптов из Table-определений |
| Auditing | `@CreatedDate`, `@LastModifiedDate`, `@CreatedBy` — автоматическое заполнение | Нет встроенного. Реализуется через `EntityHook` + `BaseEntityClass` |
| Query by Example | `findAll(Example.of(probe))` | Нет аналога |
| Spring Data REST | Автоматическое создание REST-эндпоинтов по репозиториям | Нет аналога |
| Envers (история изменений) | Hibernate Envers — аудит изменений сущностей | Нет аналога |
| L2-кэш | EhCache, Hazelcast, Infinispan через Hibernate Second Level Cache | Нет. Только entity cache внутри транзакции. Для межтранзакционного кэша — Redis, Caffeine |
| Batch-обработка | Интеграция с Spring Batch | Нет интеграции. Есть `batchInsert {}` для массовых вставок |
| R2DBC | Spring Data R2DBC — отдельный модуль с отдельным API. Связи не поддерживаются. JOIN — только через сырой SQL в `DatabaseClient` | R2DBC с версии 1.0. Те же DSL-запросы (включая JOIN) работают без изменений. DAO API недоступен |

---

## 5. Технические детали работы Exposed в Spring-проекте

### 5.1. Транзакции

При использовании `exposed-spring-boot-starter` транзакции управляются Spring через `SpringTransactionManager`. Блок `transaction {}` не нужен — используется `@Transactional`:

```kotlin
@Service
class UserService {

    @Transactional(readOnly = true)
    fun findByCity(cityName: String): List<UserDto> =
        Users.innerJoin(Cities)
            .selectAll()
            .where { Cities.name eq cityName }
            .map { UserDto(it[Users.id].value, it[Users.name], it[Users.age]) }
}
```

**Возможная ошибка:** использование `transaction {}` в Spring Boot. Этот блок открывает транзакцию напрямую через Exposed, минуя Spring. Последствия: Spring не знает о транзакции (`@TransactionalEventListener` не сработает, пропагация не работает, AOP-перехватчики не увидят транзакцию, тестовый `@Transactional` не откатит данные). Единственный оправданный случай — startup-код (`@PostConstruct`, `ApplicationRunner`), где Spring'овая инфраструктура может быть ещё не готова.

Уровни изоляции настраиваются через `@Transactional(isolation = Isolation.REPEATABLE_READ)` или глобально в `DatabaseConfig`.

### 5.2. Маппинг типов

Стандартные SQL-типы: `varchar`, `integer`, `long`, `bool`, `decimal`, `blob`, `text`.

Дата/время — три модуля на выбор:

| Модуль | Библиотека | Рекомендация |
|--------|-----------|-------------|
| `exposed-kotlin-datetime` | `kotlinx.datetime` | Для Kotlin-проектов |
| `exposed-java-time` | `java.time` | Для Java/Kotlin-mixed |
| `exposed-jodatime` | Joda-Time | Legacy, не рекомендуется |

Дополнительные типы: `exposed-json` для колонок `json`/`jsonb`, `enumerationByName()` для enum, `exposed-crypt` для прозрачного шифрования на стороне клиента.

### 5.3. DataSource

При использовании starter Exposed получает `DataSource` из Spring-контекста автоматически. Под капотом starter вызывает `Database.connect(dataSource)`. Spring Boot по умолчанию использует HikariCP как пул соединений.

### 5.4. Кэширование

В рамках одной транзакции Exposed DAO кэширует загруженные Entity. Повторный `User.findById(1)` в той же транзакции не порождает второй SQL-запрос:

```kotlin
@Transactional
fun example() {
    val user1 = User.findById(1)  // SQL: SELECT ... WHERE ID = 1
    val user2 = User.findById(1)  // Из кэша, без SQL
    // user1 === user2
}
```

Размер кэша настраивается через `DatabaseConfig.defaultMaxEntityCacheSize`. Кэш сбрасывается при завершении транзакции.

L2-кэша (аналога Hibernate Second Level Cache) нет. Для кэширования между транзакциями используются внешние решения:

```kotlin
@Service
class UserService {

    @Transactional(readOnly = true)
    @Cacheable(value = ["users"], key = "#id")
    fun findById(id: Int): UserDto? =
        Users.selectAll()
            .where { Users.id eq id }
            .firstOrNull()
            ?.let { UserDto(it[Users.id].value, it[Users.name], it[Users.age]) }

    @Transactional
    @CacheEvict(value = ["users"], key = "#id")
    fun update(id: Int, name: String) {
        Users.update({ Users.id eq id }) { it[Users.name] = name }
    }
}
```

### 5.5. Логирование

**Spring Data JPA / Hibernate:** `spring.jpa.show-sql=true` включает логирование SQL глобально. Выводит запросы с `?` вместо значений параметров. Для просмотра значений нужна дополнительная настройка (`logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE`) или сторонние инструменты (P6Spy).

**Exposed** предоставляет два встроенных логгера:

| Логгер | Назначение |
|--------|-----------|
| `StdOutSqlLogger` | `println("SQL: ...")` с подставленными значениями параметров. Для разработки |
| `Slf4jSqlDebugLogger` | Через SLF4J на уровне DEBUG. Для production |

В Spring Boot логирование включается через `spring.exposed.show-sql=true` в `application.properties` — это подключает `Slf4jSqlDebugLogger` глобально. Альтернативно, логгер можно настроить в `DatabaseConfig`:

```kotlin
@Bean
fun databaseConfig() = DatabaseConfig {
    sqlLogger = Slf4jSqlDebugLogger
}
```

Можно написать свой логгер, реализовав интерфейс `SqlLogger`:

```kotlin
object TimingLogger : SqlLogger {
    override fun log(context: StatementContext, transaction: Transaction) {
        val duration = System.currentTimeMillis() - context.startTime
        println("[${duration}ms] ${context.expandArgs(transaction)}")
    }
}
```

### 5.6. Обработка исключений

**Spring Data JPA / Hibernate:** оборачивает JDBC-исключения в иерархию `DataAccessException` автоматически через `PersistenceExceptionTranslator`. Это позволяет ловить типизированные исключения: `DataIntegrityViolationException`, `EmptyResultDataAccessException` и т.д.

> !Revision

**Exposed:** пробрасывает JDBC-исключения (`SQLException`) наружу. Собственной иерархии исключений нет. При использовании `@Transactional` в Spring Boot исключения оборачиваются в Spring-иерархию (`DataAccessException`), если настроен `PersistenceExceptionTranslationPostProcessor`. Без этой настройки — приходится ловить `ExposedSQLException` и анализировать SQL-state вручную.

### 5.7. Работа с потоками и корутинами

Транзакции в Spring Boot управляются Spring и привязаны к потоку запроса. Exposed предоставляет `newSuspendedTransaction {}` для корутин, но в контексте Spring Boot это не рекомендуется — `newSuspendedTransaction` обходит `SpringTransactionManager`, и Spring не знает о такой транзакции. Корутинная интеграция Exposed рассчитана на Ktor и другие coroutine-first фреймворки.

---

## 6. Бенчмарки: производительность Exposed vs JPA

### 6.1. Методология

Окружение: Spring Boot, PostgreSQL, JMH с warmup. Одинаковая схема данных, одинаковые индексы, один DataSource (HikariCP). Метрики: среднее время операции, p95, p99.

### 6.2. Exposed DAO vs JPA Entity

| Операция | JPA | Exposed DAO |
|----------|-----|-------------|
| Чтение по ID | `userRepository.findById(id)` | `User.findById(id)` |
| Создание | `userRepository.save(User(...))` | `User.new { ... }` |
| Обновление | `entity.name = "new"; save()` | `entity.name = "new"` (dirty tracking) |

Ожидание: производительность сопоставима — основной overhead в обоих случаях JDBC + сеть.

### 6.3. Exposed DSL vs JPQL / Criteria API

| Операция | JPA | Exposed DSL |
|----------|-----|-------------|
| Batch insert | `saveAll()` / `persist()` в цикле | `Users.batchInsert(items) { ... }` |
| SELECT с JOIN | `@Query` с JPQL | `Users.innerJoin(Cities).selectAll().where { ... }` |
| Bulk update | `@Modifying @Query` | `Users.update({ ... }) { ... }` |

Exposed DSL может быть быстрее в batch-операциях и bulk update за счёт отсутствия overhead на dirty checking и proxy-объекты.

### 6.4. Дополнительные сценарии

- Batch insert 1000+ записей: Exposed `batchInsert` vs JPA с `hibernate.jdbc.batch_size`
- Проблема N+1: в JPA проявляется через lazy loading; в Exposed DSL отсутствует — JOIN пишется явно
- Startup time: Exposed не сканирует аннотированные сущности — предположительно, быстрее
- Memory footprint: Exposed не создаёт proxy-объекты, нет L2-кэша

---

## 7. Экосистема и сообщество

### 7.1. Популярность

| Метрика | Exposed | Spring Data JPA |
|---------|---------|----------------|
| GitHub звёзды | ~9.1k | ~3k (spring-data-jpa), но часть экосистемы Spring Boot (~76k) |
| Форки | ~755 | Многократно больше |
| Возраст | ~10 лет, 1.0 в 2026 | 20+ лет (Hibernate) |

### 7.2. Динамика интереса

Запросы "Kotlin Exposed" в Google Trends показывают стабильный рост, особенно после анонса 1.0. С 2024 года — ежемесячные релизы, переход на YouTrack, выступление на KotlinConf 2025.

### 7.3. Компании

JetBrains используют Exposed в собственных production-продуктах более 8 лет. Spring Data JPA / Hibernate используются повсеместно в enterprise.

### 7.4. Документация и обучение

Exposed: новый сайт документации (jetbrains.com/help/exposed), туториалы на Baeldung, статьи на Medium. Количество материалов растёт, но пока значительно уступает JPA/Hibernate (тысячи статей, десятки книг, сотни видеокурсов). Исторически документация была слабым местом Exposed — об этом говорилось в блоге JetBrains — ситуация активно улучшается.

### 7.5. Критика и альтернативы

Позитив: лаконичность, типобезопасность, прозрачность SQL, идиоматичность для Kotlin. Критика: исторически плохая документация, долгое отсутствие стабильного релиза, ограниченная экосистема. Альтернативы в Kotlin-мире: Ktorm, JOOQ, Komapper.

### 7.6. Перспективы

Roadmap после 1.0: улучшение R2DBC-поддержки, расширение документации, поддержка Spring Boot 4. Рост Kotlin в backend-разработке — драйвер роста Exposed.

---

## 8. Выводы

### 8.1. Зрелость

Exposed стабилизировался с релизом 1.0 (январь 2026). API зафиксирован, обратная совместимость гарантирована. Экосистема (документация, обучающие материалы, сторонние расширения) уступает Spring Data JPA / Hibernate (20+ лет развития). Для Kotlin-first проектов Exposed production-ready — подтверждается опытом JetBrains.

### 8.2. Быстродействие

В большинстве сценариев производительность Exposed сопоставима с JPA. В batch-операциях и bulk update через DSL Exposed может быть быстрее за счёт отсутствия overhead на dirty checking и proxy-объекты. Меньший memory footprint, потенциально быстрее startup.

### 8.3. Функциональность

Spring Data JPA предоставляет больше из коробки: автогенерация репозиториев, Specification API, Auditing, проекции, Spring Data REST, Envers. Exposed компенсирует это типобезопасным DSL для построения запросов с проверкой компилятором.

### 8.4. Рекомендации

**Exposed** — проект на Kotlin, команда ценит контроль над SQL, используется Spring Boot с Kotlin-first подходом или Ktor.

**Spring Data JPA** — проект в Java/Kotlin-mixed среде, важна интеграция с широкой Spring-экосистемой, нужен обширный набор готовых решений.
