package com.digital.backend.model.paymentservice;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfo {
    private String fromAccountNumber;
    private String fromIFSCCode;
    private String toAccountNumber;
    private String toIFSCCode;
}

