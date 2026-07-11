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

    @Schema(description = "Account Number")
    private Long accountNumber;

    @Schema(description = "Account Type")
    private String accountType;

    @Schema(description = "Account Holder")
    private Customer accountHolder;

    @Schema(description = "Account Branch")
    private Branch accountBranch;

    @Schema(description = "Account Balance")
    private Double accountBalance;

}
