package com.digital.backend.events;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionEvent {
    private String transactionNumber;
    private String fromAccountNumber;
    private String fromIFSCCode;
    private String toAccountNumber;
    private String toIFSCCode;
    private String amount;
    private String currency;
    private String type;
    private String status;
    private String referenceNumber;
    private String timestamp;
    private String eventStatus;
    private String eventType;
}
