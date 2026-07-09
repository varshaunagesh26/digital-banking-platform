package com.db.transaction.producer;

import com.digital.backend.events.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper(); // ← inject ObjectMapper

    public void publishTransactionCreated(TransactionEvent event) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(event); // ← serialize to JSON string
            kafkaTemplate.send("transaction-events", event.getTransactionNumber(), jsonPayload);
            log.info("Published transaction event: {}", event.getTransactionNumber());
        } catch (Exception e) {
            log.error("Failed to publish transaction event: {}", e.getMessage(), e);
        }
    }
}
