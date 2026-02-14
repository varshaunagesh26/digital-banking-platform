package com.db.account.controller;

import com.db.account.model.AccountDto;

import java.util.List;

import com.db.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public AccountDto createAccountForBranchAndCustomer(@RequestParam long branchCode, @RequestParam long customerId, @Valid @RequestBody AccountDto accountDto) throws Exception {
        return accountService.createAccountForBranchAndCustomer(branchCode, customerId, accountDto);
    }

    /**
     * fina all non deleted account
     *
     * @return
     */
    @GetMapping
    public List<AccountDto> findAllAccounts() {
        return accountService.findAllActiveAccounts();
    }

    @GetMapping("/{accountNumber}")
    public AccountDto findAccountById(@PathVariable("accountNumber") Long accountId) throws Exception {
        return accountService.findAccountById(accountId);
    }

    @PatchMapping("/{accountNumber}")
    public AccountDto updatePartiallyFromDto(@PathVariable("accountNumber") long accountNumber, @Valid @RequestBody AccountDto accountDto) throws Exception {
        return accountService.updatePartiallyFromDto(accountNumber, accountDto);
    }

    @DeleteMapping("/{accountNumber}")
    public void deleteAccount(@PathVariable("accountNumber") Long accountId) throws Exception {
        accountService.deleteAccount(accountId);
    }
}