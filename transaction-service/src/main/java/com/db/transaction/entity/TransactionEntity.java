package com.db.transaction.entity;

import com.digital.backend.model.enums.Currency;
import com.digital.backend.model.enums.TransactionStatus;
import com.digital.backend.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
@Entity(name = "transaction")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_number")
    private String transactionNumber;

    @Column(name = "from_account_number")
    private Long fromAccountNumber;

    @Column(name = "from_ifsc_code")
    private String fromIFSCCode;

    @Column(name = "to_account_number")
    private Long toAccountNumber;

    @Column(name = "to_ifsc_code")
    private String toIFSCCode;

    @Column(name = "amount")
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status", nullable = false)
    private TransactionStatus status;

    @Column(name = "reference_number")
    private String referenceNumber;

    @Column(name = "time_stamp")
    private Instant timestamp;

    @Column(name = "created_at")
    Instant createdAt = Instant.now();

    @Column(name = "created_by")
    private String createdBy;

    @PrePersist
    private void prePersist() {
        UUID uuid = UUID.randomUUID();
        this.transactionNumber = uuid.toString();
    }
}
