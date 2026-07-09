package com.digitalbanking.payment.entity;

import com.digitalbanking.payment.dto.PaymentOperation;
import com.digitalbanking.payment.dto.PaymentStatus;
import com.digitalbanking.payment.dto.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentId;
    private String fromAccountNumber;
    private String toAccountNumber;
    private String fromIFSCCode;
    private String toIFSCCode;
    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    private PaymentOperation  paymentOperation;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @PrePersist
    private void prePersist() {
        UUID uuid = UUID.randomUUID();
        this.paymentId = uuid.toString();
    }
}
