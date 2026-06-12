package com.digital.backend.model;

import com.digital.backend.model.enums.EventStatus;
import com.digital.backend.model.enums.TransactionStatus;
import com.digital.backend.model.enums.TransactionType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventHistory {

    private Long id;

    private String transactionNumber;

    private TransactionType type;

    private TransactionStatus status;

    private EventStatus eventStatus;
}
