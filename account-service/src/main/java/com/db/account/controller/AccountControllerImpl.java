package com.db.account.controller;

import java.util.List;

import com.db.account.service.AccountService;
import com.digital.backend.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class AccountControllerImpl implements AccountControllerSpec {

    private final AccountService accountService;

    @Override
    public ResponseEntity<Account> createAccountForBranchAndCustomer(Long branchCode, Long customerId, Account accountDto){
        return new ResponseEntity<>(
                accountService.createAccountForBranchAndCustomer(branchCode, customerId, accountDto), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Account> getAccountByAccountNumber(Long accountNumber){
        return new ResponseEntity<>(
                accountService.getAccountByAccountNumber(accountNumber), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Account>> getActiveAccounts() {
        return new ResponseEntity<>(
                accountService.getAllActiveAccounts(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Account> updateAccount(Long accountNumber, Account accountDto){
        return new ResponseEntity<>(
                accountService.updateAccount(accountNumber, accountDto), HttpStatus.OK);
    }

    @Override
    public ResponseEntity deleteAccount(Long accountNumber){
        accountService.deleteAccount(accountNumber);
        return new ResponseEntity<>(
                HttpStatus.NO_CONTENT);
    }

}