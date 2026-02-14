package com.db.account.model;

import lombok.*;
import java.util.Date;
import java.util.List;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
    private long customerId;
    private String firstName;
    private String lastName;
    private Date customerDOB;
    private long phone;
    private String customerEmail;
    private String customerAddress;
    private List<AccountDto> accounts;
}