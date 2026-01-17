package com.db.account.controller;

import com.db.account.model.Account;
import java.util.List;
import com.db.account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;

    // Save operation
    @PostMapping("/accounts")
    public Account saveAccount(@Valid @RequestBody Account account) {
        return accountService.saveAccount(account);
    }

    // Read operation
    @GetMapping("/accounts")
    public List<Account> fetchAccountList() {
        return accountService.fetchAccountList();
    }

    // Read operation - Get by ID
    @GetMapping("/accounts/{id}")
    public Account fetchAccountById(@PathVariable("id") Long accountId) {
        return accountService.fetchAccountById(accountId);
    }

    // Update operation
    @PutMapping("/accounts/{id}")
    public Account updateAccount(@RequestBody Account account,
                                 @PathVariable("id") Long accountId) {
        return accountService.updateAccount(account, accountId);
    }

    // Delete operation
    @DeleteMapping("/accounts/{id}")
    public String deleteAccountById(@PathVariable("id") Long accountId) {
        accountService.deleteAccountById(accountId);
        return "Deleted Successfully";
    }
}