package com.db.account.entity;

import com.db.account.model.Branch;
import com.db.account.model.Customer;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long accountId;
    private String accountName;
    private String accountType;
    @OneToOne(cascade = CascadeType.ALL)
    private Customer accountHolder;
    @OneToOne(cascade = CascadeType.ALL)
    private Branch accountBranch;
}

