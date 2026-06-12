package com.db.account.listener;

import com.db.account.service.TransactionService;
import com.digital.backend.events.TransactionEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;


@RequiredArgsConstructor
@Slf4j
@Component
public class TransactionListener {

    private final ObjectMapper objectMapper =  new ObjectMapper();

    private final TransactionService  transactionService;

    @PostConstruct
    public void init() {
        log.info("TransactionListener bean created and registered");
    }

    @KafkaListener(topics = "transaction-events", groupId = "transaction-consumer-group")
    public void handleTransactionEvent(
            ConsumerRecord<String, String> record,  // ← String value now
            Acknowledgment acknowledgment) {
        try {
            log.info("Raw record received - topic: {}, partition: {}, offset: {}",
                    record.topic(), record.partition(), record.offset());

            TransactionEvent event = objectMapper.readValue(record.value(), TransactionEvent.class);

            log.info("Successfully deserialized event with txn id {}, amount: {} ", event.getTransactionNumber(), event.getAmount());

            performingDifferentTypeOfTransactions(event);

            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Error processing transaction event: {}", e.getMessage(), e);
            acknowledgment.acknowledge();
        }
    }

    private void performingDifferentTypeOfTransactions(TransactionEvent event) {

        if(event.getType() == null){
            log.info("Transaction type is null for this event: {}", event.getTransactionNumber());
            return;
        }

        log.info("Type of transaction event for this event: {}", event.getType());
        switch (event.getType()) {

            case "DEPOSIT" : transactionService.processDeposit(event);
            break;
            case "WITHDRAW" : transactionService.processWithdrawal(event);
            break;
            case "TRANSFER" : transactionService.processTransfer(event);
            break;
            default: log.info("Transaction type is unknown for this event: {}", event.getTransactionNumber());
        }
    }
}