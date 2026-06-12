package com.db.transaction.kafka;

import com.db.transaction.producer.TransactionEventProducer;
import com.digital.backend.events.TransactionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class KafkaTestTransactionController {
/*
    private final TransactionEventProducer producer;


    @PostMapping("/publish")
    public ResponseEntity<String> publish() {
        TransactionEvent event = new TransactionEvent("T01", "001", "002", "10.0", "INR", "CR", "PENDING", "T0000R1", "20260326124000", "IK");
        producer.publishTransactionCreated(event);
        return ResponseEntity.ok().body("Event published in Transaction Service...");
    }

 */
}
