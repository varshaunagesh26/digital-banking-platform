package com.db.account.entity;

import com.digital.backend.model.enums.EventStatus;
import com.digital.backend.model.enums.TransactionStatus;
import com.digital.backend.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
public class EventHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_number")
    private String transactionNumber;

    @Column(name = "transaction_type")
    private TransactionType type;

    @Column(name = "transaction_status")
    private TransactionStatus status;

    @Column(name = "event_status")
    private EventStatus eventStatus;
}
