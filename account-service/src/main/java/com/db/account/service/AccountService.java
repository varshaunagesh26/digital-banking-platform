package com.db.account.service;

import com.db.account.entity.AccountEntity;
import com.db.account.entity.BranchEntity;
import com.db.account.entity.CustomerEntity;
import com.db.account.mapper.AccountMapper;
import com.db.account.mapper.BranchMapper;
import com.db.account.mapper.CustomerMapper;
import com.db.account.mapper.CycleAvoidingMappingContext;
import com.db.account.model.AccountDto;
import com.db.account.model.BranchDto;
import com.db.account.model.CustomerDto;
import com.db.account.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class AccountService {


    private final AccountRepository accountRepository;

    private final CustomerService customerService;

    private final BranchService branchService;

    private final AccountMapper accountMapper;

    private final CustomerMapper  customerMapper;

    private final BranchMapper branchMapper;


    /**
     *
     * @param branchCode
     * @param customerId id of the customer
     * @param accountDto
     * @return
     * @throws Exception
     */
    public AccountDto createAccountForBranchAndCustomer(long branchCode, long customerId, AccountDto accountDto) throws Exception {

        BranchDto branchDto = branchService.findBranchByBranchCode(branchCode);
        CustomerDto customerDto = customerService.findCustomerByCustomerId(customerId);

        if(branchDto == null || customerDto == null){
            throw new Exception("Branch or Customer not found");
        }

        accountDto.setAccountBranch(branchDto);
        accountDto.setAccountHolder(customerDto);

        AccountEntity newAccountEntity = accountRepository.save(accountMapper.toEntity(accountDto, new CycleAvoidingMappingContext()));

        return accountMapper.toDto(newAccountEntity, new CycleAvoidingMappingContext());

    }


    /***
     *
     * @return
     */
    public List<AccountDto> findAllActiveAccounts() {

        List<AccountEntity> activeAccounts = accountRepository.findAllByIsActiveTrue();

        return activeAccounts.stream()
                .map(account -> accountMapper.toDto(account, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());

    }


    /***
     *
     * @param accountNumber
     * @return
     * @throws Exception
     */
    public AccountDto findAccountById(Long accountNumber)throws Exception{

        Optional<AccountEntity> accountByAccountNumber = accountRepository.findById(accountNumber);

        if(accountByAccountNumber.isEmpty() || accountRepository.isActiveFalse(accountNumber)){
            throw new Exception("Account with the given id not found");
        }

        return accountMapper.toDto(accountByAccountNumber.get(), new CycleAvoidingMappingContext());
    }


    public AccountDto updatePartiallyFromDto(long accountNumber, AccountDto accountDto) throws Exception {

        AccountEntity accountEntity = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new Exception("Account with the given id not found"));

        accountMapper.updateFromDtoPartially(accountDto, accountEntity);
        return accountMapper.toDto(accountEntity, new CycleAvoidingMappingContext());
    }


    /***
     *
     * @param accountNumber
     * @throws Exception
     */
    public void deleteAccount(long accountNumber) throws Exception{

        AccountEntity accountToBeDeleted = accountRepository.findById(accountNumber)
                .orElseThrow(()->new Exception("Account with the given id not found"));

        accountToBeDeleted.setIsActive(true);
        accountRepository.save(accountToBeDeleted);
    }
}
