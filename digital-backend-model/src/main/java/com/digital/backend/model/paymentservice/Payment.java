package com.digital.backend.model.paymentservice;

import com.digital.backend.model.enums.PaymentOperation;
import com.digital.backend.model.enums.PaymentType;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    private AccountInfo paymentAccountInfo;
    private Double amount;
    private PaymentType paymentType;
    private PaymentOperation paymentOperation;
}

