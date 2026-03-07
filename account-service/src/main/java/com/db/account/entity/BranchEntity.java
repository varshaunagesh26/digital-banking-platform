package com.db.account.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = "accountEntities")
@ToString(exclude = "accountEntities")
@Builder
@Entity
@Table(name = "branch")
public class BranchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "branch_code")
    private Long branchCode;

    @Column(name = "branch_name")
    private String branchName;

    @Column(name = "branch_address")
    private String branchAddress;

    @Column(name = "branch_ifsc")
    private String branchIFSC;

    @OneToMany(mappedBy = "accountBranch", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY )
    private List<AccountEntity> accountEntities = new ArrayList<>();

    @Column(name = "is_active")
    private Boolean isActive;

    @PrePersist
    public void defaultIsActive() {
        if(isActive == null) {
            isActive = Boolean.TRUE;
        }
    }

    public void addAccount(AccountEntity account) {
        accountEntities.add(account);
        account.setAccountBranch(this);
    }

    public void removeAccount(AccountEntity account) {
        accountEntities.remove(account);
        account.setAccountBranch(null);
    }
}
