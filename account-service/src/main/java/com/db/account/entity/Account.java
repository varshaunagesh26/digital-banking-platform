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
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long accountNumber;
    private String accountName;
    private String accountType;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name="accountHolderId", referencedColumnName = "id"
    )
    private Customer accountHolder;//It is reference not an actual id
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "branchId", referencedColumnName = "id"
    )
    private Branch accountBranch;
    private double accountBalance;
}