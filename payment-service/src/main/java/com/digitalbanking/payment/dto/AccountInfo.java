package com.digitalbanking.payment.dto;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountInfo {

    private String fromAccountNumber;
    private String fromIFSCCode;
    private String toAccountNumber;
    private String toIFSCCode;
}
