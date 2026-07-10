package com.db.payment.publisher;

import com.digital.backend.model.paymentservice.PaymentEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishRequestEvent(PaymentEvent paymentEvent) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(paymentEvent);
            kafkaTemplate.send("payment-request-event", paymentEvent.getPaymentId(), jsonPayload);
            log.info("Sent payment request for paymentId: {} ", paymentEvent.getPaymentId());
        } catch (Exception e) {
            log.error("Error while sending payment request for paymentId: {}", paymentEvent.getPaymentId(), e);
        }
    }
}
