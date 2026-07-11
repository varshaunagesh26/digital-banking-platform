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

    private static final String SBI_PREFIX = "SBIN";


    public Payment createPayment(Payment payment) {

        if(payment.getPaymentAccountInfo() == null
                || payment.getPaymentAccountInfo().getFromAccountNumber() == null
                || payment.getPaymentAccountInfo().getFromIFSCCode() == null)  {
            log.error("Payment creation failed: Missing from account details");
            throw new IllegalArgumentException("Missing from account details");
        }

        if (!isSbiCode(payment.getPaymentAccountInfo().getFromIFSCCode())) {
            log.error("Payment creation failed: from-IFSC {} does not belong to SBI", payment.getPaymentAccountInfo().getFromIFSCCode());
            throw new IllegalArgumentException("From account must belong to SBI (IFSC starting with SBIN)");
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

        paymentRepository.save(paymentEntity);

        if (paymentEntity.getPaymentType() == PaymentType.TRANSFER) {
            validateToAccountInfo(paymentEntity.getToAccountNumber(), paymentEntity.getToIFSCCode());

            if (!isSbiCode(paymentEntity.getToIFSCCode())) {
                log.info("Inter-bank transfer detected from {} bank to {} bank",
                        paymentEntity.getFromIFSCCode(), paymentEntity.getToIFSCCode());
                nachService.performInterBankTransfer(paymentEntity);
            } else {
                log.info("Intra-bank (SBI to SBI) transfer for account {}", paymentEntity.getFromAccountNumber());
            }
        }

        log.info("Performing {} transaction for account {} ",
                paymentEntity.getPaymentOperation(), paymentEntity.getFromAccountNumber());


        PaymentEvent paymentEvent = mapToEvent(paymentEntity);

        log.info("Payment event published for payment id {}", paymentEntity.getPaymentId());

        paymentEventPublisher.publishRequestEvent(paymentEvent);
        return paymentMapper.toDto(paymentEntity, new CycleAvoidMappingContext());
    }

    private boolean isSbiCode(String ifscCode) {
        return ifscCode != null && ifscCode.length() >= 4 && ifscCode.substring(0, 4).equals(SBI_PREFIX);
    }

    private void validateToAccountInfo(String toAccountNumber, String toIFSCCode) {
        if (toAccountNumber == null || toIFSCCode == null) {
            log.error("Payment creation failed: transfer requires toAccountNumber and toIFSCCode");
            throw new IllegalArgumentException("toAccountNumber and toIFSCCode are required for a transfer");
        }
        if (toIFSCCode.length() < 4) {
            throw new IllegalArgumentException("toIFSCCode is not a valid IFSC code");
        }
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
