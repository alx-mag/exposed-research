**Exposed Deps**
```kotlin
implementation(platform("org.jetbrains.exposed:exposed-bom:1.1.1"))
// Spring starter
implementation("org.jetbrains.exposed:exposed-spring-boot4-starter")
// Use for JDBC
implementation("org.jetbrains.exposed:exposed-jdbc")
// Use for R2DBC
implementation("org.jetbrains.exposed:exposed-r2dbc")
// Entities support
implementation("org.jetbrains.exposed:exposed-dao")
```

**Exposed CRUD**
```kotlin
class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var name by Users.name
    var email by Users.email
    var age by Users.age
    // N:1
    var city by City optionalReferencedOn Users.city
    // 1:1
    var profile by Profile optionalReferencedOn Users.profile
    // N:N
    var roles by Role via UserRoles
}

fun test(cityId: Int) {
    val moscow = City.new {  }

val alice = User.new {
    name = "Alice"
    email = "alice@example.com"
    age = 30
    city = moscow
}

    Users.insert {
        it[name] = "Alice"
        it[email] = "alice@example.com"
        it[age] = 30
        it[city] = cityId
    }
}


fun createUserExample(city: City) {
    User.new {
        name = "Alice"
        email = "alice@example.com"
        age = 30
        this.city = city
    }
}

fun readUserExample(name: String) =
    User.find {
        Users.name eq name
    }

fun findUsers(name: String?, minAge: Int?): List<User> {
    val query = Users.selectAll()
    name?.let {
        query.andWhere {
            Users.name eq it
        }
    }
    minAge?.let {
        query.andWhere {
            Users.age greaterEq it
        }
    }
    return User.wrapRows(query).toList()
}

class UserRepository {
    fun findByCityAndMinAge(
        city: String,
        minAge: Int
    ): List<User> {
        val rows = Users.innerJoin(Cities)
            .selectAll()
//            .andWhere {
//                Users.age greater minAge
//            }
//            .andWhere {
//                Cities.name eq city
//            }
            .where {
                (Users.age greater minAge) and
                        (Cities.name eq city)
            }
        return User.wrapRows(rows).toList()
    }
}

fun lazyLoadingExample() {
// Ленивая загрузка — N+1 запросов для 200 пользователей
User.all()
    .toList()
    .forEach { println(it.city?.name) }

// Жадная загрузка — 2 запроса: один для users, один для cities
User.all()
    .with(User::city)
    .toList()
    .forEach { println(it.city?.name) }
}

@Transactional
fun cacheExample() {
    val user1 = User.findById(1)  // SQL: SELECT ... WHERE ID = 1
    val user2 = User.findById(1)  // Из кэша, без SQL
    // user1 === user2
}

@Service
class MyService {

    @Transactional
    fun findUser(id: Int): User? {
        return User.findById(id)
    }

    // ❌ Better not to use Exposed transaction in Spring
    fun findUserWithExposedTransaction(id: Int): User? {
        return transaction {
            User.findById(id)
        }
    }
}
```