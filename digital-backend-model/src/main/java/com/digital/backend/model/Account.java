package com.digital.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Schema(name = "Account Number")
    private Long accountNumber;

    @Schema(name = "Account Type")
    private String accountType;

    @Schema(name = "Account Holder")
    private Customer accountHolder;

    @Schema(name = "Account Branch")
    private Branch accountBranch;

    @Schema(name = "Account Balance")
    private Double accountBalance;

}
