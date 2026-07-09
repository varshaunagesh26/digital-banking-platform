package com.db.payment.controller;

import com.db.payment.service.PaymentService;
import com.digital.backend.model.paymentservice.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PaymentControllerImpl implements PaymentControllerSpec{

    private final PaymentService paymentService;

    @Override
    public ResponseEntity<Payment> depositMoney(String fromAccountNumber, Payment deposit){
        if(deposit.getPaymentAccountInfo() == null
                || deposit.getPaymentAccountInfo().getFromAccountNumber() == null
                || deposit.getPaymentAccountInfo().getFromIFSCCode() == null){
            return ResponseEntity.badRequest()
                    .header("to account error", "toAccountNumber is required for deposit")
                    .build();
        }
        return new ResponseEntity<>(
                paymentService.createPayment(deposit), HttpStatus.OK);
    }
}
