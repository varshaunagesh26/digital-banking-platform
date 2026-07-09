package com.digitalbanking.payment.dto;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Payment {

    private AccountInfo paymentAccountInfo;
    private Double amount;
    private PaymentType paymentType;
    private PaymentOperation paymentOperation;
}
