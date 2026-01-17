package com.db.account.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class Account {
    private long accountId;
    private String accountName;
    private String accountType;
    private Customer accountHolder;
    private Branch accountBranch;
}
