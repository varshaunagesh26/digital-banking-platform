package com.digitalbanking.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentEvent {

    private String paymentId;
    private String fromAccountNumber;
    private String toAccountNumber;
    private String fromIFSCCode;
    private String toIFSCCode;
    private Double amount;
    private PaymentType paymentType;
    private PaymentOperation paymentOperation;
}
