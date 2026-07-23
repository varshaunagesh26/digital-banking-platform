package com.db.account.integration;

import com.db.account.entity.BranchEntity;
import com.db.account.entity.CustomerEntity;
import com.db.account.entity.EventHistory;
import com.db.account.repository.AccountRepository;
import com.db.account.repository.BranchRepository;
import com.db.account.repository.CustomerRepository;
import com.db.account.repository.EventHistoryRepository;
import com.db.account.service.TransactionService;
import com.digital.backend.events.TransactionEvent;
import com.digital.backend.model.Account;
import com.digital.backend.model.enums.TransactionStatus;
import com.digital.backend.model.enums.TransactionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * NOTE ON SCOPE: AccountService.createAccountForBranchAndCustomer never
 * publishes to Kafka in this codebase - only TransactionService does, in
 * response to a TransactionEvent that TransactionListener consumes from the
 * "transaction-events" topic. Since there's no embedded broker to drive
 * that consumer for real, the Kafka-verification test below calls
 * TransactionService directly (the same method the listener would call)
 * against an account created through the real REST endpoint, then verifies
 * the mocked KafkaTemplate - same Mockito.verify() pattern you asked for,
 * applied to where Kafka is actually used.
 */
@Transactional
class AccountIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EventHistoryRepository eventHistoryRepository;

    @Autowired
    private TransactionService transactionService;

    private static final Long BRANCH_CODE = 100L;
    private static final Long CUSTOMER_ID = 200L;

    @BeforeEach
    void seedBranchAndCustomer() {
        BranchEntity branch = BranchEntity.builder()
                .branchCode(BRANCH_CODE)
                .branchName("MG Road Branch")
                .branchAddress("MG Road, Bengaluru")
                .branchIFSC("DBIN0MGRD1")
                .isActive(true)
                .accountEntities(new java.util.ArrayList<>())
                .build();
        branchRepository.save(branch);

        CustomerEntity customer = CustomerEntity.builder()
                .customerId(CUSTOMER_ID)
                .firstName("Asha")
                .lastName("Rao")
                .phone(9880012345L)
                .customerEmail("asha.rao@example.com")
                .customerAddress("Indiranagar, Bengaluru")
                .isActive(true)
                .build();
        customerRepository.save(customer);
    }

    // ---------------------------------------------------------------------
    // Happy path: REST create -> Flyway-built schema actually persists it
    // ---------------------------------------------------------------------

    @Test
    void createAccount_persistsAccount_andReturns201() throws Exception {
        Account requestDto = Account.builder()
                .accountNumber(1234567890L)
                .accountType("SAVINGS")
                .accountBalance(5000.0)
                .build();

        mockMvc.perform(post("/api/v1/accounts")
                        .param("branchCode", String.valueOf(BRANCH_CODE))
                        .param("customerId", String.valueOf(CUSTOMER_ID))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountNumber").value(1234567890L))
                .andExpect(jsonPath("$.accountType").value("SAVINGS"))
                .andExpect(jsonPath("$.accountBalance").value(5000.0));

        Optional<com.db.account.entity.AccountEntity> saved =
                accountRepository.findAccountByAccountNumber(1234567890L);

        assertTrue(saved.isPresent(), "account should have been persisted against the Flyway-migrated H2 schema");
        assertEquals("SAVINGS", saved.get().getAccountType());
        assertEquals(5000.0, saved.get().getAccountBalance());
        assertTrue(saved.get().getIsActive());
        assertEquals(BRANCH_CODE, saved.get().getAccountBranch().getBranchCode());
        assertEquals(CUSTOMER_ID, saved.get().getAccountHolder().getCustomerId());
    }

    // ---------------------------------------------------------------------
    // Validation failures -> 400
    // ---------------------------------------------------------------------

    @Test
    void createAccount_missingRequiredParam_returns400() throws Exception {
        // com.digital.backend.model.Account carries no bean-validation
        // annotations (@NotNull, @NotBlank, etc.), so @Validated on the
        // controller has nothing to reject in the body itself. branchCode
        // and customerId are plain @RequestParam Long (required by
        // default), so omitting one reliably exercises Spring MVC's
        // built-in 400 handling (MissingServletRequestParameterException).
        // If you want body-level 400s too, add constraints to Account and
        // this test still works unchanged.
        Account requestDto = Account.builder()
                .accountType("SAVINGS")
                .accountBalance(1000.0)
                .build();

        mockMvc.perform(post("/api/v1/accounts")
                        .param("branchCode", String.valueOf(BRANCH_CODE))
                        // customerId intentionally omitted
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createAccount_malformedJsonBody_returns400() throws Exception {
        String malformedJson = "{ \"accountType\": \"SAVINGS\", \"accountBalance\": \"not-a-number\" }";

        mockMvc.perform(post("/api/v1/accounts")
                        .param("branchCode", String.valueOf(BRANCH_CODE))
                        .param("customerId", String.valueOf(CUSTOMER_ID))
                        .contentType("application/json")
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    // ---------------------------------------------------------------------
    // Kafka verification (see class-level note)
    // ---------------------------------------------------------------------

    @Test
    void deposit_updatesBalance_andPublishesTransactionEventToKafka() throws Exception {
        Account requestDto = Account.builder()
                .accountNumber(1111111111L)
                .accountType("SAVINGS")
                .accountBalance(2000.0)
                .build();

        mockMvc.perform(post("/api/v1/accounts")
                        .param("branchCode", String.valueOf(BRANCH_CODE))
                        .param("customerId", String.valueOf(CUSTOMER_ID))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        TransactionEvent depositEvent = new TransactionEvent();
        depositEvent.setTransactionNumber("TXN-DEPOSIT-001");
        depositEvent.setToAccountNumber("1111111111");
        depositEvent.setAmount("500.0");
        depositEvent.setType("DEPOSIT");

        // Mirrors what TransactionListener does before dispatching to processDeposit
        transactionService.setEventHistory(depositEvent, TransactionType.DEPOSIT, TransactionStatus.INPROGRESS);
        transactionService.processDeposit(depositEvent);

        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(
                eq("transaction-response-events"),
                eq("TXN-DEPOSIT-001"),
                payloadCaptor.capture());

        String publishedPayload = payloadCaptor.getValue();
        assertTrue(publishedPayload.contains("TXN-DEPOSIT-001"));
        assertTrue(publishedPayload.contains("COMPLETED"));

        Optional<EventHistory> history =
                eventHistoryRepository.findEventHistoryByTransactionNumber("TXN-DEPOSIT-001");
        assertTrue(history.isPresent());
        assertEquals(TransactionStatus.COMPLETED, history.get().getStatus());

        Optional<com.db.account.entity.AccountEntity> account =
                accountRepository.findAccountByAccountNumber(1111111111L);
        assertTrue(account.isPresent());
        assertEquals(2500.0, account.get().getAccountBalance());
    }
}