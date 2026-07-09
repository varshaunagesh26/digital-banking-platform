package com.db.payment.entity;


import com.digital.backend.model.enums.PaymentOperation;
import com.digital.backend.model.enums.PaymentStatus;
import com.digital.backend.model.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "payment")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "from_account_number")
    private String fromAccountNumber;

    @Column(name = "to_account_number")
    private String toAccountNumber;

    @Column(name = "from_ifsc_code")
    private String fromIFSCCode;

    @Column(name = "to_ifsc_code")
    private String toIFSCCode;

    @Column(name = "amount")
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_operation")
    private PaymentOperation paymentOperation;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @PrePersist
    private void prePersist() {
        UUID uuid = UUID.randomUUID();
        this.paymentId = uuid.toString();
    }
}
