package com.db.transaction.entity;

import com.digital.backend.model.enums.Currency;
import com.digital.backend.model.enums.TransactionStatus;
import jakarta.persistence.*;

import java.sql.Date;
import java.time.Instant;

@Entity(name = "transaction_audit")
public class TransactionAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    @Column(name = "id")
    private Long id;

    @Column(name = "transaction_number")
    private String transactionNumber;

    @Column(name = "from_account_number")
    private Long fromAccountNumber;

    @Column(name = "to_account_number")
    private Long toAccountNumber;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "currency")
    private Currency currency;

    @Column(name = "type")
    private String type;

    @Column(name = "status")
    private TransactionStatus status;

    @Column(name = "reference_number")
    private String referenceNumber;

    @Column(name = "time_stamp")
    private Date timestamp;

    @Column(name = "created_at")
    Instant createdAt = Instant.now();

    @Column(name = "created_by")
    private String createdBy;

}
