package com.db.account.service;

import com.db.account.mapper.AccountMapper;
import com.db.account.mapper.CycleAvoidingMappingContext;
import com.db.account.model.AccountDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.db.account.model.CustomerDto;
import com.db.account.entity.CustomerEntity;
import com.db.account.repository.CustomerRepository;
import com.db.account.mapper.CustomerMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;
    private final AccountMapper accountMapper;

    /***
     *
     * @param customerId
     * @return
     * @throws Exception
     */
    public CustomerDto findCustomerByCustomerId(long customerId) throws Exception{
        Optional<CustomerEntity> customer = customerRepository.findById(customerId);

        if(customer.isEmpty() || customerRepository.isActiveFalse(customerId)){
            throw new Exception("Customer with the given Id is not found");
        }

        return customerMapper.toDto(customer.get(), new CycleAvoidingMappingContext());
    }


    /***
     *
     * @return
     * @throws Exception
     */
    public List<CustomerDto> findAllCustomers() throws Exception{
        List<CustomerEntity> customers = customerRepository.findByIsActiveTrue();

        return customers.stream()
                .map(customer -> customerMapper.toDto(customer, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());
    }

    /***
     *
     * @param customerId
     * @return
     * @throws Exception
     */
    public List<AccountDto> findAllAccountsForCustomer(long customerId) throws Exception{

        Optional<CustomerEntity> optionalCustomer = customerRepository.findByCustomerId(customerId);

        return optionalCustomer
                .map(customerEntity ->
                        customerEntity.getAccountEntities()
                        .stream()
                                .map(accountEntity -> accountMapper.toDto(accountEntity, new CycleAvoidingMappingContext()))
                                .toList())
                .orElseThrow(() -> new Exception("Customer with the given Id is not found"));

    }

    /***
     *
     * @param customerDto
     * @return
     * @throws Exception
     */
    public CustomerDto createCustomer(CustomerDto customerDto) throws Exception{

        CustomerEntity newCustomer = customerRepository.save(customerMapper.toEntity(customerDto, new CycleAvoidingMappingContext()));
        return customerMapper.toDto(newCustomer, new CycleAvoidingMappingContext());
    }

    public CustomerDto updatePartiallyFromDto(long customerId, CustomerDto customerDto) throws Exception{

        CustomerEntity customerEntity = customerRepository.findById(customerId)
                .orElseThrow(() -> new Exception("Customer with the given Id is not found"));

        customerMapper.updatePartiallyFromDto(customerDto, customerEntity);
        return customerMapper.toDto(customerEntity, new CycleAvoidingMappingContext());
    }

    /***
     *
     * @param customerId
     * @throws Exception
     */
    public void deleteCustomer(long customerId) throws Exception{

        CustomerEntity customerToBeDeleted = customerRepository.findById(customerId)
                .orElseThrow(()-> new Exception("Customer with the given id not found"));

        customerToBeDeleted.setIsActive(true);
        customerRepository.save(customerToBeDeleted);
    }
}
