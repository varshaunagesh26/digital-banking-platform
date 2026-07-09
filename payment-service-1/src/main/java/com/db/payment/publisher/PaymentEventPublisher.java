package com.db.payment.publisher;

import com.digital.backend.model.paymentservice.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishRequestEvent(PaymentEvent paymentEvent) {
        try{
            kafkaTemplate.send("payment-request-event", paymentEvent);
            log.info("Sent payment request for paymentId: {} ", paymentEvent.getPaymentId());
        }catch(Exception e){
            log.error("Error while sending payment request for paymentId: {}", paymentEvent.getPaymentId(), e);
        }
    }
}
