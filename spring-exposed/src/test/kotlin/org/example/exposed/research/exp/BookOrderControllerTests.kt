package org.example.exposed.research.exp

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Import(TestcontainersConfiguration::class)
@ActiveProfiles("dev")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "spring.docker.compose.enabled=false",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=file:../db/01-init.sql",
        "spring.sql.init.data-locations=file:../db/02-data.sql"
    ]
)
class BookOrderControllerTests {

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Value("\${local.server.port}")
    var port: Int = 0

    private val client = HttpClient.newHttpClient()

    @BeforeEach
    fun cleanDatabase() {
        jdbcTemplate.update("delete from orders")
        jdbcTemplate.update("delete from books")
    }

    @Test
    fun `create book returns created response`() {
        val response = postJson(
            "/api/books",
            """
            {
              "title": "Effective Kotlin",
              "author": "Marcin Moskala",
              "isbn": "9788395452833",
              "year": 2019
            }
            """.trimIndent()
        )

        assertThat(response.statusCode()).isEqualTo(201)
        assertThat(response.body()).contains("\"title\":\"Effective Kotlin\"")
        assertThat(response.body()).contains("\"author\":\"Marcin Moskala\"")
        assertThat(response.body()).contains("\"isbn\":\"9788395452833\"")
        assertThat(response.body()).contains("\"year\":2019")
        assertThat(response.body()).containsPattern("\"id\":\\d+")
    }

    @Test
    fun `create order returns created response`() {
        val userId = jdbcTemplate.queryForObject(
            "select id from users order by id limit 1",
            Int::class.java
        ) ?: error("Expected at least one user in test database")

        val bookId = jdbcTemplate.queryForObject(
            "insert into books(title, author, isbn, year) values (?, ?, ?, ?) returning book_id",
            Int::class.java,
            "Domain-Driven Design",
            "Eric Evans",
            "9780321125217",
            2003
        ) ?: error("Failed to insert book for order test")

        val response = postJson(
            "/api/orders",
            """
            {
              "userId": $userId,
              "bookId": $bookId,
              "quantity": 3
            }
            """.trimIndent()
        )

        assertThat(response.statusCode()).isEqualTo(201)
        assertThat(response.body()).contains("\"userId\":$userId")
        assertThat(response.body()).contains("\"bookId\":$bookId")
        assertThat(response.body()).contains("\"quantity\":3")
        assertThat(response.body()).containsPattern("\"id\":\\d+")
    }

    @Test
    fun `get rich users returns enriched response`() {
        val response = get("/api/users/rich")

        assertThat(response.statusCode()).isEqualTo(200)
        assertThat(response.body()).contains("\"id\":1")
        assertThat(response.body()).contains("\"name\":\"User 1\"")
        assertThat(response.body()).contains("\"email\":\"user1@example.com\"")
        assertThat(response.body()).contains("\"age\":19")
        assertThat(response.body()).contains("\"roles\":[\"USER\"]")
        assertThat(response.body()).contains("\"city\":\"Samara\"")
        assertThat(response.body()).contains("\"profileBio\":\"Bio for mock user #1\"")
    }

    private fun postJson(path: String, body: String): HttpResponse<String> {
        val request = HttpRequest.newBuilder()
            .uri(URI("http://localhost:$port$path"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()

        return client.send(request, HttpResponse.BodyHandlers.ofString())
    }

    private fun get(path: String): HttpResponse<String> {
        val request = HttpRequest.newBuilder()
            .uri(URI("http://localhost:$port$path"))
            .GET()
            .build()

        return client.send(request, HttpResponse.BodyHandlers.ofString())
    }
}
