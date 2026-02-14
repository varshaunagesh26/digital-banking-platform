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
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "account_number")
    private long accountNumber;

    @Column(name = "account_type")
    private String accountType;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name="accountHolderId", referencedColumnName = "id"
    )
    private CustomerEntity accountHolder;//It is reference not an actual id

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "branchId", referencedColumnName = "id"
    )
    private BranchEntity accountBranch;

    @Column(name = "account_balance")
    private double accountBalance;

    @Column(name = "is_active")
    private Boolean isActive;
}