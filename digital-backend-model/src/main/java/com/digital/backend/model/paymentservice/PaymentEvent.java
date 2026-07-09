package com.digital.backend.model.paymentservice;

import com.digital.backend.model.enums.PaymentOperation;
import com.digital.backend.model.enums.PaymentType;
import lombok.*;

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