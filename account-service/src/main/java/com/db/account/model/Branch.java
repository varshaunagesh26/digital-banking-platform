package com.db.account.model;

import com.db.account.model.Branch;
import com.db.account.model.Customer;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@Builder

public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long branchId;
    private String branchName;
    private String branchAddress;
    private String branchIFSC;

}
