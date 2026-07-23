package com.db.account.unit;

import com.db.account.entity.AccountEntity;
import com.db.account.entity.BranchEntity;
import com.db.account.service.AccountService;
import com.db.account.entity.CustomerEntity;
import com.db.account.mapper.AccountMapper;
import com.db.account.mapper.CycleAvoidingMappingContext;
import com.db.account.repository.AccountRepository;
import com.db.account.repository.BranchRepository;
import com.db.account.repository.CustomerRepository;
import com.digital.backend.exceptions.EntityAlreadyDeletedException;
import com.digital.backend.exceptions.EntityNotFoundException;
import com.digital.backend.model.Account;
import com.digital.backend.model.Branch;
import com.digital.backend.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AccountService}.
 * <p>
 * Uses pure Mockito (no Spring context) to keep tests fast and isolated.
 * Downstream collaborators (repositories, mapper) are mocked; only the
 * service logic under test is real.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService Unit Tests")
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;

    // Common fixtures reused across tests
    private BranchEntity branchEntity;
    private CustomerEntity customerEntity;
    private AccountEntity accountEntity;
    private Account accountDto;

    private static final Long BRANCH_CODE = 100L;
    private static final Long CUSTOMER_ID = 200L;
    private static final Long ACCOUNT_NUMBER = 300L;

    @BeforeEach
    void setUp() {
        branchEntity = BranchEntity.builder()
                .id(1L)
                .branchCode(BRANCH_CODE)
                .branchName("Main Branch")
                .branchAddress("123 Main St")
                .branchIFSC("IFSC0001")
                .accountEntities(new ArrayList<>())
                .isActive(true)
                .build();

        customerEntity = CustomerEntity.builder()
                .id(1L)
                .customerId(CUSTOMER_ID)
                .firstName("John")
                .lastName("Doe")
                .customerEmail("john.doe@example.com")
                .customerAddress("456 Elm St")
                .phone(9999999999L)
                .customerAccounts(new ArrayList<>())
                .isActive(true)
                .build();

        accountEntity = AccountEntity.builder()
                .id(1L)
                .accountNumber(ACCOUNT_NUMBER)
                .accountType("SAVINGS")
                .accountBalance(1000.0)
                .accountHolder(customerEntity)
                .accountBranch(branchEntity)
                .isActive(true)
                .build();

        accountDto = Account.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .accountType("SAVINGS")
                .accountBalance(1000.0)
                .build();
    }

    // ------------------------------------------------------------------
    // createAccountForBranchAndCustomer
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("createAccountForBranchAndCustomer")
    class CreateAccountForBranchAndCustomer {

        @Test
        @DisplayName("Happy path: creates account when branch and customer exist")
        void shouldCreateAccountSuccessfully() {
            // Arrange
            when(branchRepository.findBranchByBranchCode(BRANCH_CODE))
                    .thenReturn(Optional.of(branchEntity));
            when(customerRepository.findCustomerByCustomerId(CUSTOMER_ID))
                    .thenReturn(Optional.of(customerEntity));
            when(accountMapper.toEntity(any(Account.class), any(CycleAvoidingMappingContext.class)))
                    .thenReturn(accountEntity);
            when(accountRepository.save(any(AccountEntity.class)))
                    .thenReturn(accountEntity);
            when(accountMapper.toDto(any(AccountEntity.class), any(CycleAvoidingMappingContext.class)))
                    .thenReturn(accountDto);

            // Act
            Account result = accountService.createAccountForBranchAndCustomer(BRANCH_CODE, CUSTOMER_ID, accountDto);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getAccountNumber()).isEqualTo(ACCOUNT_NUMBER);
            assertThat(result.getAccountType()).isEqualTo("SAVINGS");

            verify(branchRepository, times(1)).findBranchByBranchCode(BRANCH_CODE);
            verify(customerRepository, times(1)).findCustomerByCustomerId(CUSTOMER_ID);
            verify(accountRepository, times(1)).save(any(AccountEntity.class));
        }

        @Test
        @DisplayName("Happy path: links new account to branch and customer entities")
        void shouldLinkAccountToBranchAndCustomer() {
            // Arrange
            when(branchRepository.findBranchByBranchCode(BRANCH_CODE))
                    .thenReturn(Optional.of(branchEntity));
            when(customerRepository.findCustomerByCustomerId(CUSTOMER_ID))
                    .thenReturn(Optional.of(customerEntity));
            when(accountMapper.toEntity(any(Account.class), any(CycleAvoidingMappingContext.class)))
                    .thenReturn(accountEntity);
            when(accountRepository.save(any(AccountEntity.class)))
                    .thenReturn(accountEntity);
            when(accountMapper.toDto(any(AccountEntity.class), any(CycleAvoidingMappingContext.class)))
                    .thenReturn(accountDto);

            // Act
            accountService.createAccountForBranchAndCustomer(BRANCH_CODE, CUSTOMER_ID, accountDto);

            // Assert - the bidirectional helper methods should have wired the association
            assertThat(branchEntity.getAccountEntities()).contains(accountEntity);
            assertThat(customerEntity.getCustomerAccounts()).contains(accountEntity);
        }

        @Test
        @DisplayName("Edge case: throws EntityNotFoundException when branch does not exist")
        void shouldThrowWhenBranchNotFound() {
            // Arrange
            when(branchRepository.findBranchByBranchCode(BRANCH_CODE))
                    .thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> accountService.createAccountForBranchAndCustomer(BRANCH_CODE, CUSTOMER_ID, accountDto));

            assertThat(exception.getMessage()).contains("Branch not found");
            verify(customerRepository, never()).findCustomerByCustomerId(anyLong());
            verify(accountRepository, never()).save(any(AccountEntity.class));
        }

        @Test
        @DisplayName("Edge case: throws EntityNotFoundException when customer does not exist")
        void shouldThrowWhenCustomerNotFound() {
            // Arrange
            when(branchRepository.findBranchByBranchCode(BRANCH_CODE))
                    .thenReturn(Optional.of(branchEntity));
            when(customerRepository.findCustomerByCustomerId(CUSTOMER_ID))
                    .thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> accountService.createAccountForBranchAndCustomer(BRANCH_CODE, CUSTOMER_ID, accountDto));

            assertThat(exception.getMessage()).contains("Customer not found");
            verify(accountRepository, never()).save(any(AccountEntity.class));
        }
    }

    // ------------------------------------------------------------------
    // getAllActiveAccounts
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("getAllActiveAccounts")
    class GetAllActiveAccounts {

        @Test
        @DisplayName("Happy path: returns mapped list of active accounts")
        void shouldReturnAllActiveAccounts() {
            // Arrange
            AccountEntity secondEntity = AccountEntity.builder()
                    .id(2L)
                    .accountNumber(301L)
                    .accountType("CURRENT")
                    .accountBalance(5000.0)
                    .isActive(true)
                    .build();
            Account secondDto = Account.builder()
                    .accountNumber(301L)
                    .accountType("CURRENT")
                    .accountBalance(5000.0)
                    .build();

            when(accountRepository.findAllByIsActiveTrue())
                    .thenReturn(List.of(accountEntity, secondEntity));
            when(accountMapper.toDto(any(AccountEntity.class), any(CycleAvoidingMappingContext.class)))
                    .thenReturn(accountDto, secondDto);

            // Act
            List<Account> result = accountService.getAllActiveAccounts();

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result).extracting(Account::getAccountNumber)
                    .containsExactly(ACCOUNT_NUMBER, 301L);
            verify(accountRepository, times(1)).findAllByIsActiveTrue();
        }

        @Test
        @DisplayName("Edge case: returns empty list when no active accounts exist")
        void shouldReturnEmptyListWhenNoActiveAccounts() {
            // Arrange
            when(accountRepository.findAllByIsActiveTrue())
                    .thenReturn(Collections.emptyList());

            // Act
            List<Account> result = accountService.getAllActiveAccounts();

            // Assert
            assertThat(result).isEmpty();
            verify(accountMapper, never()).toDto(any(AccountEntity.class), any(CycleAvoidingMappingContext.class));
        }
    }

    // ------------------------------------------------------------------
    // getAccountByAccountNumber
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("getAccountByAccountNumber")
    class GetAccountByAccountNumber {

        @Test
        @DisplayName("Happy path: returns account when found and active")
        void shouldReturnAccountWhenFoundAndActive() {
            // Arrange
            when(accountRepository.findAccountByAccountNumber(ACCOUNT_NUMBER))
                    .thenReturn(Optional.of(accountEntity));
            when(accountMapper.toDto(eq(accountEntity), any(CycleAvoidingMappingContext.class)))
                    .thenReturn(accountDto);

            // Act
            Account result = accountService.getAccountByAccountNumber(ACCOUNT_NUMBER);

            // Assert
            assertThat(result).isNotNull();
            assertEquals(ACCOUNT_NUMBER, result.getAccountNumber());
        }

        @Test
        @DisplayName("Edge case: throws EntityNotFoundException when account does not exist")
        void shouldThrowWhenAccountNotFound() {
            // Arrange
            when(accountRepository.findAccountByAccountNumber(ACCOUNT_NUMBER))
                    .thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> accountService.getAccountByAccountNumber(ACCOUNT_NUMBER));

            assertThat(exception.getMessage()).contains("not found or Account is Inactive");
            verify(accountMapper, never()).toDto(any(AccountEntity.class), any(CycleAvoidingMappingContext.class));
        }

        @Test
        @DisplayName("Edge case: throws EntityNotFoundException when account is inactive")
        void shouldThrowWhenAccountIsInactive() {
            // Arrange
            accountEntity.setIsActive(false);
            when(accountRepository.findAccountByAccountNumber(ACCOUNT_NUMBER))
                    .thenReturn(Optional.of(accountEntity));

            // Act & Assert
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> accountService.getAccountByAccountNumber(ACCOUNT_NUMBER));

            assertThat(exception.getMessage()).contains(String.valueOf(ACCOUNT_NUMBER));
            verify(accountMapper, never()).toDto(any(AccountEntity.class), any(CycleAvoidingMappingContext.class));
        }
    }

    // ------------------------------------------------------------------
    // updateAccount
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("updateAccount")
    class UpdateAccount {

        @Test
        @DisplayName("Happy path: updates account fields via mapper")
        void shouldUpdateAccountSuccessfully() {
            // Arrange
            Account updateDto = Account.builder()
                    .accountType("CURRENT")
                    .accountBalance(2500.0)
                    .build();

            when(accountRepository.findAccountByAccountNumber(ACCOUNT_NUMBER))
                    .thenReturn(Optional.of(accountEntity));
            when(accountMapper.toDto(eq(accountEntity), any(CycleAvoidingMappingContext.class)))
                    .thenReturn(updateDto);

            // Act
            Account result = accountService.updateAccount(ACCOUNT_NUMBER, updateDto);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getAccountType()).isEqualTo("CURRENT");
            verify(accountMapper, times(1)).updateFromDtoPartially(updateDto, accountEntity);
        }

        @Test
        @DisplayName("Happy path: allows null accountDto (no-op partial update)")
        void shouldHandleNullAccountDto() {
            // Arrange
            when(accountRepository.findAccountByAccountNumber(ACCOUNT_NUMBER))
                    .thenReturn(Optional.of(accountEntity));
            when(accountMapper.toDto(eq(accountEntity), any(CycleAvoidingMappingContext.class)))
                    .thenReturn(accountDto);

            // Act
            Account result = accountService.updateAccount(ACCOUNT_NUMBER, null);

            // Assert
            assertThat(result).isNotNull();
            verify(accountMapper, times(1)).updateFromDtoPartially(null, accountEntity);
        }

        @Test
        @DisplayName("Edge case: throws EntityNotFoundException when account does not exist")
        void shouldThrowWhenAccountNotFound() {
            // Arrange
            when(accountRepository.findAccountByAccountNumber(ACCOUNT_NUMBER))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(EntityNotFoundException.class,
                    () -> accountService.updateAccount(ACCOUNT_NUMBER, accountDto));

            verify(accountMapper, never()).updateFromDtoPartially(any(), any());
        }

        @Test
        @DisplayName("Edge case: throws EntityNotFoundException when branch code is 0 (invalid branch update)")
        void shouldThrowWhenBranchCodeIsZero() {
            // Arrange
            Branch invalidBranch = Branch.builder().branchCode(0L).build();
            Account updateDto = Account.builder().accountBranch(invalidBranch).build();

            when(accountRepository.findAccountByAccountNumber(ACCOUNT_NUMBER))
                    .thenReturn(Optional.of(accountEntity));

            // Act & Assert
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> accountService.updateAccount(ACCOUNT_NUMBER, updateDto));

            assertThat(exception.getMessage()).contains("branch code not found");
            verify(accountMapper, never()).updateFromDtoPartially(any(), any());
        }

        @Test
        @DisplayName("Edge case: throws EntityNotFoundException when customer id is 0 (invalid holder update)")
        void shouldThrowWhenCustomerIdIsZero() {
            // Arrange
            Customer invalidCustomer = Customer.builder().customerId(0L).build();
            Account updateDto = Account.builder().accountHolder(invalidCustomer).build();

            when(accountRepository.findAccountByAccountNumber(ACCOUNT_NUMBER))
                    .thenReturn(Optional.of(accountEntity));

            // Act & Assert
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> accountService.updateAccount(ACCOUNT_NUMBER, updateDto));

            assertThat(exception.getMessage()).contains("customer id does not exist");
            verify(accountMapper, never()).updateFromDtoPartially(any(), any());
        }
    }

    // ------------------------------------------------------------------
    // deleteAccount
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("deleteAccount")
    class DeleteAccount {

        @Test
        @DisplayName("Happy path: soft-deletes an active account")
        void shouldSoftDeleteActiveAccount() {
            // Arrange
            when(accountRepository.findAccountByAccountNumber(ACCOUNT_NUMBER))
                    .thenReturn(Optional.of(accountEntity));
            when(accountRepository.save(any(AccountEntity.class)))
                    .thenReturn(accountEntity);

            // Act
            accountService.deleteAccount(ACCOUNT_NUMBER);

            // Assert
            ArgumentCaptor<AccountEntity> captor = ArgumentCaptor.forClass(AccountEntity.class);
            verify(accountRepository, times(1)).save(captor.capture());
            assertThat(captor.getValue().getIsActive()).isFalse();
        }

        @Test
        @DisplayName("Edge case: throws EntityNotFoundException when account does not exist")
        void shouldThrowWhenAccountNotFound() {
            // Arrange
            when(accountRepository.findAccountByAccountNumber(ACCOUNT_NUMBER))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(EntityNotFoundException.class,
                    () -> accountService.deleteAccount(ACCOUNT_NUMBER));

            verify(accountRepository, never()).save(any(AccountEntity.class));
        }

        @Test
        @DisplayName("Edge case: throws EntityAlreadyDeletedException when account is already inactive")
        void shouldThrowWhenAccountAlreadyInactive() {
            // Arrange
            accountEntity.setIsActive(false);
            when(accountRepository.findAccountByAccountNumber(ACCOUNT_NUMBER))
                    .thenReturn(Optional.of(accountEntity));

            // Act & Assert
            EntityAlreadyDeletedException exception = assertThrows(EntityAlreadyDeletedException.class,
                    () -> accountService.deleteAccount(ACCOUNT_NUMBER));

            assertThat(exception.getMessage()).contains(String.valueOf(ACCOUNT_NUMBER));
            verify(accountRepository, never()).save(any(AccountEntity.class));
        }
    }
}