package com.digitalbanking.payment.service;

import com.digitalbanking.payment.dto.Payment;
import com.digitalbanking.payment.dto.PaymentEvent;
import com.digitalbanking.payment.dto.PaymentStatus;
import com.digitalbanking.payment.dto.PaymentType;
import com.digitalbanking.payment.entity.PaymentEntity;
import com.digitalbanking.payment.mapper.CycleAvoidMappingContext;
import com.digitalbanking.payment.mapper.PaymentMapper;
import com.digitalbanking.payment.nach.NachService;
import com.digitalbanking.payment.publisher.PaymentEventPublisher;
import com.digitalbanking.payment.repository.PaymentRepository;
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


        PaymentEvent  paymentEvent = PaymentEvent.builder()
                .paymentId(paymentEntity.getPaymentId())
                .fromAccountNumber(paymentEntity.getFromAccountNumber())
                .fromIFSCCode(paymentEntity.getFromIFSCCode())
                .toAccountNumber(paymentEntity.getToAccountNumber())
                .toIFSCCode(paymentEntity.getToIFSCCode())
                .amount(paymentEntity.getAmount())
                .paymentType(paymentEntity.getPaymentType())
                .paymentOperation(paymentEntity.getPaymentOperation())
                .build();

        log.info("Payment event published for payment id {}", paymentEntity.getPaymentId());

        paymentEventPublisher.publishRequestEvent(paymentEvent);
        return paymentMapper.toDto(paymentEntity, new CycleAvoidMappingContext());
    }
}
