package com.db.transaction.scheduler;

import com.db.transaction.producer.TransactionEventProducer;
import com.db.transaction.service.TransactionService;
import com.digital.backend.events.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionScheduler {

    private final TransactionEventProducer transactionEventProducer;

    private final TransactionService transactionService;


    @Scheduled(cron = "0 */15 * * * *")
    public void processTransaction(){

        log.info("Scheduler triggered at {}", LocalDateTime.now());

        List<TransactionEvent> events = transactionService.getTransactionBatch();

        if(events.isEmpty()){
            log.info("No transactions found");
            return;
        }

        log.info("Found {} transaction(s) to process", events.size());

        for (TransactionEvent event : events) {
            try {
                transactionEventProducer.publishTransactionCreated(event);
                log.info("Transaction {} processed successfully", event.getTransactionNumber());
            } catch (Exception e) {
                log.error("Failed to process the transaction {}: {}", event.getTransactionNumber(), e.getMessage());
            }
        }
    }
}
