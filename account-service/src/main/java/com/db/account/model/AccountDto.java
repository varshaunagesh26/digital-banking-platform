package com.db.account.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {

    @Schema(name = "Account Number")
    private Long accountNumber;

    @Schema(name = "Account Type")
    private String accountType;

    @Schema(name = "Account Holder")
    private CustomerDto accountHolder;

    @Schema(name = "Account Branch")
    private BranchDto accountBranch;

    @Schema(name = "Account Balance")
    private Double accountBalance;

}