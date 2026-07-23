package com.db.account.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "customerAccounts")
@ToString(exclude = "customerAccounts")
@Entity
@Table(name = "customer")
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "customer_dob")
    private Date customerDOB;

    private Long phone;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "customer_address")
    private String customerAddress;

    @OneToMany(mappedBy = "accountHolder", cascade = CascadeType.ALL,  orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<AccountEntity> customerAccounts = new ArrayList<>();

    @Column(name = "is_active")
    private Boolean isActive;

    @PrePersist
    public void defaultIsActive() {
        if(isActive == null) {
            isActive = Boolean.TRUE;
        }
    }

    public void addAccount(AccountEntity account) {
        customerAccounts.add(account);
        account.setAccountHolder(this);
    }

    public void removeAccount(AccountEntity account) {
        customerAccounts.remove(account);
        account.setAccountHolder(null);
    }
}