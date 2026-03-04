package com.db.account.service;

import com.db.account.exceptions.EntityAlreadyDeletedException;
import com.db.account.exceptions.EntityNotFoundException;
import com.db.account.mapper.AccountMapper;
import com.db.account.mapper.CycleAvoidingMappingContext;
import com.db.account.model.AccountDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.db.account.model.CustomerDto;
import com.db.account.entity.CustomerEntity;
import com.db.account.repository.CustomerRepository;
import com.db.account.mapper.CustomerMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    private final AccountMapper accountMapper;

    public CustomerDto createCustomer(CustomerDto customerDto){

        log.atInfo().log("creating a customer");

        CustomerEntity customerEntity = customerMapper.toEntity(customerDto, new CycleAvoidingMappingContext());
        CustomerEntity newCustomer = customerRepository.save(customerEntity);
        return customerMapper.toDto(newCustomer, new CycleAvoidingMappingContext());
    }

    public CustomerDto getCustomerByCustomerId(Long customerId){

        log.atInfo().log("getting customer by customer id: {}", customerId);

        Optional<CustomerEntity> customer = customerRepository.findCustomerByCustomerId(customerId);

        if (customer.isEmpty() || !customer.get().getIsActive()) {
            log.atError().log("customer with customer id: {} not found or is inactive", customerId);

            throw new EntityNotFoundException("Customer with the given Id:" + customerId + " is not found or is inactive");
        }

        return customerMapper.toDto(customer.get(), new CycleAvoidingMappingContext());
    }

    public List<CustomerDto> getAllCustomers(){

        log.atInfo().log("getting all customers");

        List<CustomerEntity> customers = customerRepository.findByIsActiveTrue();

        return customers.stream()
                .map(customer -> customerMapper.toDto(customer, new CycleAvoidingMappingContext()))
                .toList();
    }

    public List<AccountDto> getAllAccountsForCustomer(Long customerId){

        log.atInfo().log("getting account for customer id: {}", customerId);

        Optional<CustomerEntity> optionalCustomer = customerRepository.findCustomerByCustomerId(customerId);

        return optionalCustomer
                .map(customerEntity ->
                        customerEntity.getCustomerAccounts()
                                .stream()
                                .map(accountEntity -> accountMapper.toDto(accountEntity, new CycleAvoidingMappingContext()))
                                .toList())
                .orElseThrow(() -> new EntityNotFoundException("Customer with the given Id is not found: " + customerId));

    }

    public CustomerDto updateCustomer(Long customerId, CustomerDto customerDto){

        log.atInfo().log("updating customer with customer id: {}", customerId);

        CustomerEntity existingCustomer = customerRepository.findCustomerByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer with the given Id is not found: " + customerId));

        customerMapper.updateFromDtoPartially(customerDto, existingCustomer);
        return customerMapper.toDto(existingCustomer, new CycleAvoidingMappingContext());
    }

    public void deleteCustomer(Long customerId){

        log.atInfo().log("deleting customer with customer id: {}", customerId);

        customerRepository.findCustomerByCustomerId(customerId)
                .map(customerEntity -> {
                    if (!customerEntity.getIsActive()) {
                        // already deleted
                        throw new EntityAlreadyDeletedException("Customer with the given Id is not found: " + customerId);
                    }
                    customerEntity.setIsActive(false);
                    return customerRepository.save(customerEntity);
                })
                .orElseThrow(() -> new EntityNotFoundException("Customer with the given id not found: " + customerId));

    }
}
