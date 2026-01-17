package com.db.account.service;

import com.db.account.model.Account;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    //Save operation
    public Account saveAccount(Account account) {
        return null;
    }

    // Read operation - Get all accounts
    public List<Account> fetchAccountList() {
        return null;
    }

    // Read operation - Get account by ID
    public Account fetchAccountById(Long accountId){
        return null;
    }

    // Update operation
    public Account updateAccount(Account account, Long accountId){
        return null;
    }

    // Delete operation
    public void deleteAccountById(Long accountId) {
       // return null;
    }
}
