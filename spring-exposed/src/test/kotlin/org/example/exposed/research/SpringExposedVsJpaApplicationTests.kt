package org.example.exposed.research

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class SpringExposedVsJpaApplicationTests {

    @Test
    fun contextLoads() {
    }

}
