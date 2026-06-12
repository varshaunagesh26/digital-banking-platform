package com.db.account.response.publisher;

import com.digital.backend.events.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Component
@Slf4j
public class TransactionEventResponsePublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishResponseEvent(TransactionEvent transactionEvent) {
        try{
            String jsonPayload = objectMapper.writeValueAsString(transactionEvent);
            kafkaTemplate.send("transaction-response-events", transactionEvent.getTransactionNumber(), jsonPayload);
            log.info("Published transaction event: {}", transactionEvent.getTransactionNumber());
        }catch(Exception e){
            log.error("Failed to publish transaction event: {}", transactionEvent.getTransactionNumber(), e);
        }
    }
}
