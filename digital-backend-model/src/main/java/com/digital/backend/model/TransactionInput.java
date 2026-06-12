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
    Long fromAccountNumber;
    Double amount;
}
