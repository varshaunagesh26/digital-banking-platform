package com.db.transaction.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Common base class for all integration tests in transaction-service.
 *
 * - Boots the full Spring context with MockMvc wired in (no real servlet container).
 * - Overrides datasource/JPA/Flyway/Kafka properties directly via @TestPropertySource
 *   so the context never touches production Postgres or a real broker. These
 *   properties are applied last and win over anything in application.yml, so they
 *   don't depend on a profile-specific file being discovered on the classpath.
 * - hibernate.default_schema and datasource.hikari.schema are both pointed at H2's
 *   PUBLIC schema here, overriding "transaction" from the main application.yml.
 *   Blanking them out doesn't work — Hikari still calls connection.setSchema("")
 *   with an empty string, which H2 rejects as a schema that doesn't exist.
 * - Replaces the real KafkaTemplate bean with a Mockito mock so the context never
 *   tries to talk to a real broker. Individual tests can inject this same instance
 *   (it's the same mock everywhere thanks to Spring's bean override mechanism) and
 *   use Mockito.verify(...) on it.
 *
 * Note: @MockBean is removed as of this Spring Boot version; @MockitoBean is its
 * direct replacement and requires no additional dependency beyond spring-boot-starter-test,
 * which is already on the test classpath.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.default_schema=PUBLIC",
        "spring.datasource.hikari.schema=PUBLIC",
        "spring.flyway.enabled=false",
        "spring.kafka.listener.auto-startup=false",
        "services.account-service.url=http://localhost:8081"
})
public abstract class BaseIntegrationTest {

    @MockitoBean
    protected KafkaTemplate<String, Object> kafkaTemplate;

}