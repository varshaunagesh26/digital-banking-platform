package com.db.payment.nach;

import com.db.payment.entity.PaymentEntity;
import com.db.payment.publisher.PaymentEventPublisher;
import com.digital.backend.model.paymentservice.PaymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NachServiceImpl implements NachService{

    @Autowired
    private PaymentEventPublisher paymentEventPublisher;

    public void performInterBankTransfer(PaymentEntity paymentEntity) {

        log.info("Inter bank transfer is being performed through NACH from account {} to {}", paymentEntity.getFromAccountNumber(), paymentEntity.getToIFSCCode().substring(0, 4));

        paymentEntity.setToAccountNumber(null);
        paymentEntity.setToIFSCCode(null);
    }
}
