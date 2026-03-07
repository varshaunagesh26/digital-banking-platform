package com.db.account.controller;

import com.db.account.service.CustomerService;
import com.digital.backend.model.Account;
import com.digital.backend.model.Customer;
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
    public ResponseEntity<Customer> createCustomer(Customer customerDto){
        return new ResponseEntity<>(
                customerService.createCustomer(customerDto), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Customer> getCustomerByCustomerId(Long customerId){
        return new ResponseEntity<>(
                customerService.getCustomerByCustomerId(customerId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Customer>> getAllCustomers(){
        return new ResponseEntity<>(
                customerService.getAllCustomers(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Account>> getAllAccountsForCustomer(Long customerId){
        return new ResponseEntity<>(
                customerService.getAllAccountsForCustomer(customerId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Customer> updateCustomer(Long customerId, Customer customerDto){
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
