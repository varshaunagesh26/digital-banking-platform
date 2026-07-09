package com.digitalbanking.payment.publisher;

import com.digitalbanking.payment.dto.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishRequestEvent(PaymentEvent paymentEvent) {
        try{
            String jsonPayload = objectMapper.writeValueAsString(paymentEvent);
            kafkaTemplate.send("payment-request-event", paymentEvent);
            log.info("Sent payment request for paymentId: {} ", paymentEvent.getPaymentId());
        }catch(Exception e){
            log.error("Error while sending payment request for paymentId: {}", paymentEvent.getPaymentId(), e);
        }
    }
}
