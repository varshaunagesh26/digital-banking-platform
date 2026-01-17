package com.db.account.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import com.db.account.model.Branch;
import com.db.account.model.Customer;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Data
@Builder
@Getter
@Setter

public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long customerId;
    private String customerName;
    private Date customerDOB;
    private long phone;
    private double accountBalance;
    private String customerEmail;

}
