package com.db.account.controller;

import com.db.account.model.AccountDto;
import com.db.account.service.CustomerService;
import com.db.account.model.CustomerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
public class CustomerControllerImpl implements CustomerControllerSpec {

    private final CustomerService customerService;

    @Override
    public ResponseEntity<CustomerDto> createCustomer(CustomerDto customerDto){
        return new ResponseEntity<>(
                customerService.createCustomer(customerDto), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<CustomerDto> getCustomerByCustomerId(Long customerId){
        return new ResponseEntity<>(
                customerService.getCustomerByCustomerId(customerId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<CustomerDto>> getAllCustomers(){
        return new ResponseEntity<>(
                customerService.getAllCustomers(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<AccountDto>> getAllAccountsForCustomer(Long customerId){
        return new ResponseEntity<>(
                customerService.getAllAccountsForCustomer(customerId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<CustomerDto> updateCustomer(Long customerId, CustomerDto customerDto){
        return new ResponseEntity<>(
                customerService.updateCustomer(customerId, customerDto), HttpStatus.OK);
    }

    @Override
    public ResponseEntity deleteCustomer(Long customerId){
        customerService.deleteCustomer(customerId);
        return new ResponseEntity<>(
                HttpStatus.NO_CONTENT);
    }
}
