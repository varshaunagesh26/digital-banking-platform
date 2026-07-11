package com.db.transaction.listener;

import com.db.transaction.service.TransactionService;
import com.digital.backend.events.TransactionEvent;
import com.digital.backend.model.paymentservice.PaymentEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class TransactionListener {

    private final ObjectMapper objectMapper =  new ObjectMapper();

    private final TransactionService transactionService;

    @KafkaListener(topics = "transaction-response-events", groupId = "debug-group")
    public void handleTransactionEvent(
            ConsumerRecord<String, String> record,
            Acknowledgment acknowledgment) {
        try {
            log.info("Account event response received - topic: {}, partition: {}, offset: {}",
                    record.topic(), record.partition(), record.offset());

            TransactionEvent event = objectMapper.readValue(record.value(), TransactionEvent.class);

            log.info("Received response for transaction: {}, status: {}",
                    event.getTransactionNumber(), event.getStatus());

            transactionService.updateTransactionResponseStatus(event);

            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Error processing transaction response: {}", e.getMessage(), e);
            acknowledgment.acknowledge();
        }
    }

    @KafkaListener(topics = "payment-request-event", groupId = "transaction-consumer-group")
    public void handlePaymentRequestEvent(
            ConsumerRecord<String, String> record,
            Acknowledgment acknowledgment){
        try{
            log.info("Payment event received- topic: {}, partition: {}, offset: {}",
                    record.topic(), record.partition(), record.offset());

            PaymentEvent event = objectMapper.readValue(record.value(), PaymentEvent.class);

            log.info("Processing payment event for paymentId: {}", event.getPaymentId());

            transactionService.processPaymentEvent(event);

            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Error processing payment event: {}", e.getMessage(), e);
            acknowledgment.acknowledge();
        }
    }
}
