package com.db.payment.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Common base for all payment-service integration tests.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Boots the full Spring context (controller -> service -> repository -> mapper)
 *         with MockMvc wired in, so HTTP requests can be simulated end-to-end.</li>
 *     <li>Redirects both {@code spring.datasource.*} AND {@code spring.flyway.*} to the
 *         SAME named in-memory H2 instance ("testdb"), so the production PostgreSQL
 *         instance defined in application.yml is never touched. Note that in this
 *         project's application.yml, Flyway has its own independent url/user/password
 *         block (it does NOT automatically reuse spring.datasource), so both must be
 *         overridden or Flyway would still try to reach the real Postgres box.</li>
 *     <li>Sets {@code hibernate.ddl-auto=validate} so schema creation is proven to come
 *         from Flyway migrations, not from Hibernate auto-DDL.</li>
 *     <li>Replaces the real {@link KafkaTemplate} bean with a Mockito mock, since no
 *         embedded/test Kafka broker is available. Declared here (not in the subclass)
 *         so it is mocked consistently across the whole test suite.</li>
 * </ul>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(properties = {

        // ---- Datasource: in-memory H2 standing in for PostgreSQL ----
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",

        // Flyway owns schema creation. Hibernate does not validate the mapping here:
        // the `serial` id column in the migration creates an INTEGER in Postgres/H2,
        // while PaymentEntity.id is mapped as Long (BIGINT) — a real, pre-existing
        // mismatch that "validate" correctly flags. Turned off for tests only.
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.jpa.properties.hibernate.default_schema=payment",

        // ---- Flyway: application.yml points this at a hard-coded Postgres URL,
        //      so it must be overridden separately, pointed at the SAME named
        //      H2 instance as the datasource above ("testdb") so both share state ----
        "spring.flyway.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.flyway.user=sa",
        "spring.flyway.password=",
        "spring.flyway.schemas=payment",
        "spring.flyway.create-schemas=true",
        "spring.flyway.locations=classpath:db/migration",
        "spring.flyway.baseline-on-migrate=true",
        "spring.flyway.baseline-version=0"
})
public abstract class BaseIntegrationTest {

    /**
     * Mocks the Kafka producer bean everywhere in the test context. Subclasses use
     * Mockito.verify(kafkaTemplate)... to assert publishing behavior without needing
     * a running/embedded broker.
     *
     * Note: Spring Boot 4.x deprecates/removes the old @MockBean in favor of
     * @MockitoBean (org.springframework.test.context.bean.override.mockito.MockitoBean),
     * which is what's used here.
     */
    @MockitoBean
    protected KafkaTemplate<String, Object> kafkaTemplate;
}