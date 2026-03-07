package com.db.account.controller;


import com.digital.backend.model.Account;
import com.digital.backend.model.Customer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


public interface CustomerControllerSpec {

    @PostMapping(path = "/api/v1/customers",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new customer", tags = "customer")
    @ApiResponse(
            responseCode = "201",
            description = "Customer created",
            content = @Content(schema = @Schema(implementation = Customer.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Customer updating",
            required = true,
            content = @Content(schema = @Schema(implementation = Customer.class))
    )
    ResponseEntity<Customer> createCustomer(
            @RequestBody Customer customerDto);


    @GetMapping(path = "/api/v1/customers",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Return all customers", tags = "customers")
    @ApiResponse(
            responseCode = "200",
            description = "Returned all customers",
            content = @Content(schema = @Schema(implementation = Customer.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input")
    ResponseEntity<List<Customer>> getAllCustomers();


    @GetMapping(path = "/api/v1/customers/{customerId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Return customer by customer id", tags = "customer")
    @ApiResponse(
            responseCode = "200",
            description = "Returned customer by customer id",
            content = @Content(schema = @Schema(implementation = Customer.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input")
    ResponseEntity<Customer> getCustomerByCustomerId(
            @Parameter(description = "Customer Id", required = true)
            @PathVariable Long customerId);

    @GetMapping(path = "/api/v1/customers/{customerId}/accounts",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Return all accounts for a customer", tags = "accounts")
    @ApiResponse(
            responseCode = "200",
            description = "Returned all accounts of a customer",
            content = @Content(schema = @Schema(implementation = Customer.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input")
    ResponseEntity<List<Account>> getAllAccountsForCustomer(
            @Parameter(description = "Customer Id", required = true)
            @PathVariable Long customerId);


    @PatchMapping(path = "/api/v1/customers/{customerId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update customer", tags = "customer")
    @ApiResponse(
            responseCode = "200",
            description = "Customer is updated",
            content = @Content(schema = @Schema(implementation = Customer.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input")
    ResponseEntity<Customer> updateCustomer(
            @Parameter(description = "Customer Id", required = true)
            @PathVariable Long customerId,
            @RequestBody Customer customerDto);


    @DeleteMapping("/api/v1/customers/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete customer", tags = "customer")
    @ApiResponse(
            responseCode = "204",
            description = "Customer deleted",
            content = @Content(schema = @Schema(implementation = Customer.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input")
    ResponseEntity<Customer> deleteCustomer(
            @Parameter(description = "Customer Id", required = true)
            @PathVariable Long customerId);
}
