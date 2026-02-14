package com.db.account.controller;

import com.db.account.model.AccountDto;
import com.db.account.service.CustomerService;
import com.db.account.model.CustomerDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/{customerId}")
    public CustomerDto findCustomerByCustomerId(@PathVariable("customerId") long customerId) throws Exception{
        return customerService.findCustomerByCustomerId(customerId);
    }

    @GetMapping
    public List<CustomerDto> findAllCustomers() throws Exception{
        return customerService.findAllCustomers();
    }

    @GetMapping("/{customerId}")
    public List<AccountDto> findAllAccountsForCustomer(@PathVariable long customerId) throws Exception{
        return customerService.findAllAccountsForCustomer(customerId);
    }

    @PostMapping
    public CustomerDto createCustomer(@RequestBody CustomerDto customerDto) throws Exception{
        return customerService.createCustomer(customerDto);
    }

    @PatchMapping("/{customerId}")
    public CustomerDto updateCustomerName(@PathVariable("customerId") long customerId, @Valid @RequestBody CustomerDto customerDto) throws Exception{
        return customerService.updatePartiallyFromDto(customerId, customerDto);
    }

    @DeleteMapping("/{customerId}")
    public void deleteCustomer(@PathVariable("customerId") long customerId) throws Exception{
        customerService.deleteCustomer(customerId);
    }
}
