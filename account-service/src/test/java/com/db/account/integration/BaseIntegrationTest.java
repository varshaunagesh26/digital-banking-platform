package com.db.account.integration;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Shared scaffolding for account-service integration tests.
 *
 * What this gives every subclass:
 *  - A full Spring context (no servlet container) with MockMvc wired up.
 *  - An in-memory H2 datasource (PostgreSQL compatibility mode) so Flyway
 *    runs your real migrations from classpath:db/migrations against H2
 *    instead of your Postgres box. spring.jpa.hibernate.ddl-auto is "none"
 *    on purpose - Flyway, not Hibernate, owns the schema, exactly like prod.
 *    All of this is configured in application-test.yml (activated via
 *    @ActiveProfiles("test") below) - kept in one place instead of being
 *    duplicated here.
 *  - KafkaTemplate<String, Object> replaced with a Mockito mock, so
 *    TransactionEventResponsePublisher.publishResponseEvent(...) never
 *    dials a real broker; subclasses just Mockito.verify(kafkaTemplate)....
 *  - Real @KafkaListener containers are prevented from starting via
 *    spring.kafka.listener.auto-startup=false in application-test.yml.
 *    This uses your ACTUAL production ConcurrentKafkaListenerContainerFactory
 *    bean unmodified - no test-only container factory bean is created here,
 *    so there's no risk of it drifting out of sync with your real Kafka
 *    consumer config (e.g. if its generic type or settings change later).
 *
 * Nothing here touches pom.xml or any production @Configuration class.
 *
 * ASSUMPTION: your Flyway scripts under db/migrations are H2-compatible
 * under MODE=PostgreSQL. If they use Postgres-only DDL (e.g. native JSONB,
 * gin indexes, certain SERIAL/sequence forms), Flyway will fail fast on
 * context startup with a clear SQL error - that's a migration-script fix,
 * not a test-config fix.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    /** Universally mocked - verify(kafkaTemplate)... in any subclass, no broker required. */
    @MockitoBean
    protected KafkaTemplate<String, Object> kafkaTemplate;
}