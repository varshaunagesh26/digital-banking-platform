package com.db.account.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BranchDto {
    private long branchCode;
    private String branchName;
    private String branchAddress;
    private String branchIFSC;
    private List<AccountDto> accounts;
}