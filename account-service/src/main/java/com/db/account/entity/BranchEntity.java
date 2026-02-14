package com.db.account.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
public class BranchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "branch_code")
    private long branchCode;

    @Column(name = "branch_name")
    private String branchName;

    @Column(name = "branch_address")
    private String branchAddress;

    @Column(name = "branch_ifsc")
    private String branchIFSC;

    @OneToMany(mappedBy = "accountBranch")
    private List<AccountEntity> accountEntities;

    @Column(name = "is_active")
    private Boolean isActive;

}
