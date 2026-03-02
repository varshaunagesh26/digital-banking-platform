package com.db.account.controller;

import com.db.account.model.AccountDto;

import java.util.List;

import com.db.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class AccountControllerImpl implements AccountControllerSpec {

    private final AccountService accountService;

    @Override
    public ResponseEntity<AccountDto> createAccountForBranchAndCustomer(Long branchCode, Long customerId, AccountDto accountDto) throws Exception {
        return new ResponseEntity<>(
                accountService.createAccountForBranchAndCustomer(branchCode, customerId, accountDto), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<AccountDto> getAccountByAccountNumber(Long accountNumber) throws Exception {
        return new ResponseEntity<>(
                accountService.getAccountByAccountNumber(accountNumber), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<AccountDto>> getActiveAccounts() {
        return new ResponseEntity<>(
                accountService.getAllActiveAccounts(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AccountDto> updateAccount(Long accountNumber, AccountDto accountDto) throws Exception {
        return new ResponseEntity<>(
                accountService.updateAccount(accountNumber, accountDto), HttpStatus.OK);
    }

    @Override
    public ResponseEntity deleteAccount(Long accountNumber) throws Exception {
        accountService.deleteAccount(accountNumber);
        return new ResponseEntity<>(
                HttpStatus.NO_CONTENT);
    }

}