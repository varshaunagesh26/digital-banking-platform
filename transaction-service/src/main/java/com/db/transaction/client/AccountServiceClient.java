package com.db.transaction.client;

import com.digital.backend.model.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "account-service",
        url = "${services.account-service.url}")
public interface AccountServiceClient {

    @GetMapping(value = "/api/v1/{accountNumber}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> getAccountByAccountNumber(
            @PathVariable Long accountNumber);

    @GetMapping(value = "/api/v1/accounts", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<Account>> getAccounts();

}
