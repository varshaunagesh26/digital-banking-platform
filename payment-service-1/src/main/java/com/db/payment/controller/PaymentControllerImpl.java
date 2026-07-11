package com.db.payment.controller;

import com.db.payment.service.PaymentService;
import com.digital.backend.model.paymentservice.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PaymentControllerImpl implements PaymentControllerSpec{

    private final PaymentService paymentService;

    @Override
    public ResponseEntity<Payment> payment(String fromAccountNumber, Payment payment){
        if(payment.getPaymentAccountInfo() == null
                || payment.getPaymentAccountInfo().getFromAccountNumber() == null
                || payment.getPaymentAccountInfo().getFromIFSCCode() == null){
            log.warn("Rejected payment request: missing fromAccountNumber/fromIFSCCode in payload {}", payment);

            return ResponseEntity.badRequest()
                    .header("to account error", "toAccountNumber is required for deposit")
                    .build();
        }
        return new ResponseEntity<>(
                paymentService.createPayment(payment), HttpStatus.OK);
    }
}
