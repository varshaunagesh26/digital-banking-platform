package com.digital.backend.model;

import lombok.*;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionInput {
    Long toAccountNumber;
    String toIFSCCode;
    Long fromAccountNumber;
    String fromIFSCCode;
    Double amount;
}
