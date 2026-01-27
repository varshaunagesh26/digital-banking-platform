package com.db.account.model;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    private Long accountNumber;
    private String accountName;
    private String accountType;
    private Customer accountHolder;
    private Branch accountBranch;
    private double accountBalance;

}