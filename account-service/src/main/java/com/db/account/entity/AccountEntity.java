package com.db.account.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account")
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number")
    private Long accountNumber;

    @Column(name = "account_type")
    private String accountType;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(
            name="customerId", referencedColumnName = "id"
    )
    private CustomerEntity accountHolder;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(
            name = "branchId", referencedColumnName = "id"
    )
    private BranchEntity accountBranch;

    @Column(name = "account_balance")
    private Double accountBalance;

    @Column(name = "is_active")
    private Boolean isActive;

    @PrePersist
    public void defaultIsActive() {
        if(isActive == null) {
            isActive = Boolean.TRUE;
        }
    }
}