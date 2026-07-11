package com.db.account.listener;

import com.db.account.service.TransactionService;
import com.digital.backend.events.TransactionEvent;
import com.digital.backend.model.enums.TransactionStatus;
import com.digital.backend.model.enums.TransactionType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLIntegrityConstraintViolationException;


@RequiredArgsConstructor
@Slf4j
@Component
public class TransactionListener {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final TransactionService transactionService;

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

            executeTransactions(event);

            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Error processing transaction event: {}", e.getMessage(), e);
            acknowledgment.acknowledge();
        }
    }

    private void executeTransactions(TransactionEvent event){

        if (event.getType() == null) {
            log.info("Transaction type is null for this event: {}", event.getTransactionNumber());
            return;
        }

        if (transactionService.isAlreadyProcessed(event.getTransactionNumber())) {
            log.info("Transaction {} already processed, skipping", event.getTransactionNumber());
            transactionService.republishExistingResult(event.getTransactionNumber());
            return;
        }

        log.info("Type of transaction event for this event: {}", event.getType());
        try {
            transactionService.setEventHistory(event, TransactionType.valueOf(event.getType()), TransactionStatus.INPROGRESS);
        } catch (Exception e) {
            log.warn("Transaction already exists, skipping creation: {}", event.getTransactionNumber());
            log.info("Attempting to republish result for: {}", event.getTransactionNumber());
            transactionService.republishExistingResult(event.getTransactionNumber());
            return;
        }


        switch (event.getType()) {

            case "DEPOSIT":
                transactionService.processDeposit(event);
                break;
            case "WITHDRAW":
                transactionService.processWithdrawal(event);
                break;
            case "TRANSFER":
                transactionService.processTransfer(event);
                break;
            default:
                log.info("Transaction type is unknown for this event: {}", event.getTransactionNumber());
        }
    }
}