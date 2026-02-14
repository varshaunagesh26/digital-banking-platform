package com.db.account.model;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
    private Long accountNumber;
    private String accountType;
    private CustomerDto accountHolder;
    private BranchDto accountBranch;
    private double accountBalance;

}