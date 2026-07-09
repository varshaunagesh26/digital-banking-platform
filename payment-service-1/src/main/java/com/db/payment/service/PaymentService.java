package com.db.payment.service;


import com.db.payment.entity.PaymentEntity;
import com.db.payment.mapper.CycleAvoidMappingContext;
import com.db.payment.mapper.PaymentMapper;
import com.db.payment.nach.NachService;
import com.db.payment.publisher.PaymentEventPublisher;
import com.db.payment.repository.PaymentRepository;
import com.digital.backend.model.enums.PaymentStatus;
import com.digital.backend.model.enums.PaymentType;
import com.digital.backend.model.paymentservice.Payment;
import com.digital.backend.model.paymentservice.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Slf4j
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final PaymentMapper paymentMapper;

    private final PaymentEventPublisher paymentEventPublisher;

    private final NachService nachService;

    public Payment createPayment(Payment payment) {

        if(payment.getPaymentAccountInfo().getFromAccountNumber() == null ||
                payment.getPaymentAccountInfo().getFromIFSCCode() == null) {
            log.error("Payment creation failed: Missing from account details");
            throw new IllegalArgumentException("Missing from account details");
        }

        PaymentEntity paymentEntity = PaymentEntity.builder()
                .fromAccountNumber(payment.getPaymentAccountInfo().getFromAccountNumber())
                .fromIFSCCode(payment.getPaymentAccountInfo().getFromIFSCCode())
                .toAccountNumber(payment.getPaymentAccountInfo().getToAccountNumber())
                .toIFSCCode(payment.getPaymentAccountInfo().getToIFSCCode())
                .amount(payment.getAmount())
                .paymentType(payment.getPaymentType())
                .paymentOperation(payment.getPaymentOperation())
                .paymentStatus(PaymentStatus.INITIATED)
                .build();

        if (paymentEntity.getFromIFSCCode() == null || paymentEntity.getFromIFSCCode().length() < 4) {
            log.warn("Invalid or missing From-IFSC code provided.");
            return null;
        }

        if(!paymentEntity.getFromIFSCCode().substring(0,4).equals("SBIN")){
            log.info("This account doesn't belong to SBI bank");
            return null;
        }

        paymentRepository.save(paymentEntity);

        if(!paymentEntity.getToIFSCCode().substring(0,4).equals("SBIN") && paymentEntity.getPaymentType() == PaymentType.TRANSFER){
            log.info("Inter-bank transfer is detected from {} bank to {} bank", paymentEntity.getFromIFSCCode(),paymentEntity.getToIFSCCode());
            nachService.performInterBankTransfer(paymentEntity);
        }

        log.info("Performing {} transaction for account {} ",  paymentEntity.getPaymentOperation(), paymentEntity.getFromAccountNumber());


        PaymentEvent paymentEvent = mapToEvent(paymentEntity);

        log.info("Payment event published for payment id {}", paymentEntity.getPaymentId());

        paymentEventPublisher.publishRequestEvent(paymentEvent);
        return paymentMapper.toDto(paymentEntity, new CycleAvoidMappingContext());
    }

    private PaymentEvent mapToEvent(PaymentEntity paymentEntity){
        PaymentEvent event = new PaymentEvent();
        event.setPaymentId(paymentEntity.getPaymentId() != null
                ? paymentEntity.getPaymentId() : null);
        event.setFromAccountNumber(paymentEntity.getFromAccountNumber() != null
                ? paymentEntity.getFromAccountNumber() : null);
        event.setFromIFSCCode(paymentEntity.getFromIFSCCode() != null
                ? paymentEntity.getFromIFSCCode() : null);
        event.setToAccountNumber(paymentEntity.getToAccountNumber());
        event.setToIFSCCode(paymentEntity.getToIFSCCode());
        event.setAmount(paymentEntity.getAmount() != null
                ? paymentEntity.getAmount() : null);
        event.setPaymentType(paymentEntity.getPaymentType() != null
                ? paymentEntity.getPaymentType() : null);
        event.setPaymentOperation(paymentEntity.getPaymentOperation() != null
                ? paymentEntity.getPaymentOperation() : null);
        return event;
    }
}
