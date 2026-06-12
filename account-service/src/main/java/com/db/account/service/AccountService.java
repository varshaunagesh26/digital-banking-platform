package com.db.account.service;

import com.db.account.entity.AccountEntity;
import com.db.account.entity.BranchEntity;
import com.db.account.entity.CustomerEntity;
import com.db.account.mapper.AccountMapper;
import com.db.account.mapper.CycleAvoidingMappingContext;
import com.db.account.repository.AccountRepository;
import com.db.account.repository.BranchRepository;
import com.db.account.repository.CustomerRepository;
import com.digital.backend.exceptions.EntityAlreadyDeletedException;
import com.digital.backend.exceptions.EntityNotFoundException;
import com.digital.backend.model.Account;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    private final BranchRepository branchRepository;

    private final CustomerRepository customerRepository;

    private final AccountMapper accountMapper;


    public Account createAccountForBranchAndCustomer(Long branchCode, Long customerId, Account accountDto){

        log.atInfo().log("creating account for customer with id: {} for branch: {}", customerId, branchCode);

        BranchEntity branchEntity = branchRepository.findBranchByBranchCode(branchCode)
                .orElseThrow(() -> new EntityNotFoundException("Branch not found: " + branchCode));
        CustomerEntity customerEntity = customerRepository.findCustomerByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + customerId));

        AccountEntity accountEntity = accountMapper.toEntity(accountDto, new CycleAvoidingMappingContext());
        branchEntity.addAccount(accountEntity);
        customerEntity.addAccount(accountEntity);

        AccountEntity newAccountEntity = accountRepository.save(accountEntity);
        return accountMapper.toDto(newAccountEntity, new CycleAvoidingMappingContext());

    }

    public List<Account> getAllActiveAccounts() {

        log.atInfo().log("getting all active accounts");

        List<AccountEntity> activeAccounts = accountRepository.findAllByIsActiveTrue();

        return activeAccounts.stream()
                .map(account ->
                        accountMapper.toDto(account, new CycleAvoidingMappingContext()))
                .toList();

    }

    public Account getAccountByAccountNumber(Long accountNumber){

        log.atInfo().log("getting account by account number: {}", accountNumber);

        Optional<AccountEntity> optionalAccount = accountRepository.findAccountByAccountNumber(accountNumber);

        if (optionalAccount.isEmpty() || !optionalAccount.get().getIsActive()) {
            log.atError().log("account number not found: {}", accountNumber);

            throw new EntityNotFoundException("Account with the given id not found or Account is Inactive : " + accountNumber);
        }
        return accountMapper.toDto(optionalAccount.get(), new CycleAvoidingMappingContext());
    }


    public Account updateAccount(Long accountNumber, Account accountDto){

        log.atInfo().log("updating account by account number: {}", accountNumber);

        AccountEntity accountEntity = accountRepository.findAccountByAccountNumber(accountNumber)
                .orElseThrow(() -> new EntityNotFoundException("Account with the given id not found: " + accountNumber));

        //if branch exists only in case of branch update
        if (accountDto != null && accountDto.getAccountBranch() != null && accountDto.getAccountBranch().getBranchCode() == 0L) {
            log.atError().log("branch with the branch code not found: {}", accountDto.getAccountBranch().getBranchCode());

            throw new EntityNotFoundException("Trying to update account branch but branch code not found: " + accountDto.getAccountBranch().getBranchCode());
        }

        //if customer exists only in case of customer update
        if (accountDto != null && accountDto.getAccountHolder() != null && accountDto.getAccountHolder().getCustomerId() == 0L) {
            log.atError().log("customer with the customer id not found: {}", accountDto.getAccountHolder().getCustomerId());

            throw new EntityNotFoundException("Trying to update account holder but customer id does not exist: " + accountDto.getAccountHolder().getCustomerId());
        }

        accountMapper.updateFromDtoPartially(accountDto, accountEntity);
        return accountMapper.toDto(accountEntity, new CycleAvoidingMappingContext());
    }

    public void deleteAccount(Long accountNumber) {

        log.atInfo().log("deleting account by account number: {}", accountNumber);

        accountRepository.findAccountByAccountNumber(accountNumber)
                .map(accountEntity -> {
                    if(!accountEntity.getIsActive()) {
                        throw new EntityAlreadyDeletedException("Account with the given id not found:  " + accountNumber);
                    }
                    accountEntity.setIsActive(false);
                    return accountRepository.save(accountEntity);
                })
                .orElseThrow(() -> new EntityNotFoundException("Account with the given id not found: " + accountNumber));
    }
}
