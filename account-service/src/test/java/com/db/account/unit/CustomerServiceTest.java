package com.db.account.unit;

import com.db.account.entity.AccountEntity;
import com.db.account.entity.CustomerEntity;
import com.db.account.service.CustomerService;
import com.db.account.mapper.AccountMapper;
import com.db.account.mapper.CustomerMapper;
import com.db.account.mapper.CycleAvoidingMappingContext;
import com.db.account.repository.CustomerRepository;
import com.digital.backend.exceptions.EntityAlreadyDeletedException;
import com.digital.backend.exceptions.EntityNotFoundException;
import com.digital.backend.model.Account;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link CustomerService}.
 * <p>
 * Pure Mockito tests (no Spring context loaded). Downstream collaborators
 * (repository, mappers) are mocked; only the service logic under test is real.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerService Unit Tests")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private CustomerService customerService;

    // Common fixtures reused across tests
    private CustomerEntity customerEntity;
    private Customer customerDto;
    private AccountEntity accountEntity;
    private Account accountDto;

    private static final Long CUSTOMER_ID = 200L;
    private static final Long ACCOUNT_NUMBER = 300L;

    @BeforeEach
    void setUp() {
        // NOTE: customerAccounts is explicitly initialized here because Lombok's
        // @Builder bypasses the field's inline "= new ArrayList<>()" initializer,
        // which would otherwise leave the list null.
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

        customerDto = Customer.builder()
                .customerId(CUSTOMER_ID)
                .firstName("John")
                .lastName("Doe")
                .customerEmail("john.doe@example.com")
                .customerAddress("456 Elm St")
                .phone(9999999999L)
                .build();

        accountEntity = AccountEntity.builder()
                .id(1L)
                .accountNumber(ACCOUNT_NUMBER)
                .accountType("SAVINGS")
                .accountBalance(1000.0)
                .accountHolder(customerEntity)
                .isActive(true)
                .build();

        accountDto = Account.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .accountType("SAVINGS")
                .accountBalance(1000.0)
                .build();
    }

    // ------------------------------------------------------------------
    // createCustomer
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("createCustomer")
    class CreateCustomer {

        @Test
        @DisplayName("Happy path: creates and returns the new customer")
        void shouldCreateCustomerSuccessfully() {
            // Arrange
            when(customerMapper.toEntity(eq(customerDto), any(CycleAvoidingMappingContext.class)))
                    .thenReturn(customerEntity);
            when(customerRepository.save(any(CustomerEntity.class)))
                    .thenReturn(customerEntity);
            when(customerMapper.toDto(eq(customerEntity), any(CycleAvoidingMappingContext.class)))
                    .thenReturn(customerDto);

            // Act
            Customer result = customerService.createCustomer(customerDto);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCustomerId()).isEqualTo(CUSTOMER_ID);
            assertThat(result.getFirstName()).isEqualTo("John");
            verify(customerRepository, times(1)).save(any(CustomerEntity.class));
        }

        @Test
        @DisplayName("Edge case: propagates mapper's entity to repository unmodified")
        void shouldPersistMappedEntity() {
            // Arrange
            when(customerMapper.toEntity(eq(customerDto), any(CycleAvoidingMappingContext.class)))
                    .thenReturn(customerEntity);
            when(customerRepository.save(any(CustomerEntity.class)))
                    .thenReturn(customerEntity);
            when(customerMapper.toDto(eq(customerEntity), any(CycleAvoidingMappingContext.class)))
                    .thenReturn(customerDto);

            // Act
            customerService.createCustomer(customerDto);

            // Assert
            ArgumentCaptor<CustomerEntity> captor = ArgumentCaptor.forClass(CustomerEntity.class);
            verify(customerRepository).save(captor.capture());
            assertThat(captor.getValue().getCustomerId()).isEqualTo(CUSTOMER_ID);
        }
    }

    // ------------------------------------------------------------------
    // getCustomerByCustomerId
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("getCustomerByCustomerId")
    class GetCustomerByCustomerId {

        @Test
        @DisplayName("Happy path: returns customer when found and active")
        void shouldReturnCustomerWhenFoundAndActive() {
            // Arrange
            when(customerRepository.findCustomerByCustomerId(CUSTOMER_ID))
                    .thenReturn(Optional.of(customerEntity));
            when(customerMapper.toDto(eq(customerEntity), any(CycleAvoidingMappingContext.class)))
                    .thenReturn(customerDto);

            // Act
            Customer result = customerService.getCustomerByCustomerId(CUSTOMER_ID);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCustomerId()).isEqualTo(CUSTOMER_ID);
        }

        @Test
        @DisplayName("Edge case: throws EntityNotFoundException when customer does not exist")
        void shouldThrowWhenCustomerNotFound() {
            // Arrange
            when(customerRepository.findCustomerByCustomerId(CUSTOMER_ID))
                    .thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> customerService.getCustomerByCustomerId(CUSTOMER_ID));

            assertThat(exception.getMessage()).contains("not found or is inactive");
            verify(customerMapper, never()).toDto(any(CustomerEntity.class), any(CycleAvoidingMappingContext.class));
        }

        @Test
        @DisplayName("Edge case: throws EntityNotFoundException when customer is inactive")
        void shouldThrowWhenCustomerIsInactive() {
            // Arrange
            customerEntity.setIsActive(false);
            when(customerRepository.findCustomerByCustomerId(CUSTOMER_ID))
                    .thenReturn(Optional.of(customerEntity));

            // Act & Assert
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> customerService.getCustomerByCustomerId(CUSTOMER_ID));

            assertThat(exception.getMessage()).contains(String.valueOf(CUSTOMER_ID));
            verify(customerMapper, never()).toDto(any(CustomerEntity.class), any(CycleAvoidingMappingContext.class));
        }
    }

    // ------------------------------------------------------------------
    // getAllCustomers
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("getAllCustomers")
    class GetAllCustomers {

        @Test
        @DisplayName("Happy path: returns mapped list of active customers")
        void shouldReturnAllActiveCustomers() {
            // Arrange
            CustomerEntity secondEntity = CustomerEntity.builder()
                    .id(2L)
                    .customerId(201L)
                    .firstName("Jane")
                    .lastName("Smith")
                    .customerEmail("jane.smith@example.com")
                    .customerAddress("789 Oak St")
                    .phone(8888888888L)
                    .customerAccounts(new ArrayList<>())
                    .isActive(true)
                    .build();
            Customer secondDto = Customer.builder()
                    .customerId(201L)
                    .firstName("Jane")
                    .lastName("Smith")
                    .customerEmail("jane.smith@example.com")
                    .customerAddress("789 Oak St")
                    .phone(8888888888L)
                    .build();

            when(customerRepository.findByIsActiveTrue())
                    .thenReturn(List.of(customerEntity, secondEntity));
            when(customerMapper.toDto(any(CustomerEntity.class), any(CycleAvoidingMappingContext.class)))
                    .thenReturn(customerDto, secondDto);

            // Act
            List<Customer> result = customerService.getAllCustomers();

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result).extracting(Customer::getCustomerId)
                    .containsExactly(CUSTOMER_ID, 201L);
            verify(customerRepository, times(1)).findByIsActiveTrue();
        }

        @Test
        @DisplayName("Edge case: returns empty list when no active customers exist")
        void shouldReturnEmptyListWhenNoActiveCustomers() {
            // Arrange
            when(customerRepository.findByIsActiveTrue())
                    .thenReturn(Collections.emptyList());

            // Act
            List<Customer> result = customerService.getAllCustomers();

            // Assert
            assertThat(result).isEmpty();
            verify(customerMapper, never()).toDto(any(CustomerEntity.class), any(CycleAvoidingMappingContext.class));
        }
    }

    // ------------------------------------------------------------------
    // getAllAccountsForCustomer
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("getAllAccountsForCustomer")
    class GetAllAccountsForCustomer {

        @Test
        @DisplayName("Happy path: returns mapped accounts belonging to the customer")
        void shouldReturnAccountsForCustomer() {
            // Arrange
            customerEntity.getCustomerAccounts().add(accountEntity);

            when(customerRepository.findCustomerByCustomerId(CUSTOMER_ID))
                    .thenReturn(Optional.of(customerEntity));
            when(accountMapper.toDto(eq(accountEntity), any(CycleAvoidingMappingContext.class)))
                    .thenReturn(accountDto);

            // Act
            List<Account> result = customerService.getAllAccountsForCustomer(CUSTOMER_ID);

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getAccountNumber()).isEqualTo(ACCOUNT_NUMBER);
        }

        @Test
        @DisplayName("Edge case: returns empty list when customer has no accounts")
        void shouldReturnEmptyListWhenCustomerHasNoAccounts() {
            // Arrange - customerEntity fixture already has an empty customerAccounts list
            when(customerRepository.findCustomerByCustomerId(CUSTOMER_ID))
                    .thenReturn(Optional.of(customerEntity));

            // Act
            List<Account> result = customerService.getAllAccountsForCustomer(CUSTOMER_ID);

            // Assert
            assertThat(result).isEmpty();
            verify(accountMapper, never()).toDto(any(AccountEntity.class), any(CycleAvoidingMappingContext.class));
        }

        @Test
        @DisplayName("Edge case: throws EntityNotFoundException when customer does not exist")
        void shouldThrowWhenCustomerNotFound() {
            // Arrange
            when(customerRepository.findCustomerByCustomerId(CUSTOMER_ID))
                    .thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> customerService.getAllAccountsForCustomer(CUSTOMER_ID));

            assertThat(exception.getMessage()).contains(String.valueOf(CUSTOMER_ID));
        }
    }

    // ------------------------------------------------------------------
    // updateCustomer
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("updateCustomer")
    class UpdateCustomer {

        @Test
        @DisplayName("Happy path: updates customer fields via mapper")
        void shouldUpdateCustomerSuccessfully() {
            // Arrange
            Customer updateDto = Customer.builder()
                    .firstName("Jonathan")
                    .build();

            when(customerRepository.findCustomerByCustomerId(CUSTOMER_ID))
                    .thenReturn(Optional.of(customerEntity));
            when(customerMapper.toDto(eq(customerEntity), any(CycleAvoidingMappingContext.class)))
                    .thenReturn(updateDto);

            // Act
            Customer result = customerService.updateCustomer(CUSTOMER_ID, updateDto);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getFirstName()).isEqualTo("Jonathan");
            verify(customerMapper, times(1)).updateFromDtoPartially(updateDto, customerEntity);
        }

        @Test
        @DisplayName("Happy path: allows null customerDto (no-op partial update)")
        void shouldHandleNullCustomerDto() {
            // Arrange
            when(customerRepository.findCustomerByCustomerId(CUSTOMER_ID))
                    .thenReturn(Optional.of(customerEntity));
            when(customerMapper.toDto(eq(customerEntity), any(CycleAvoidingMappingContext.class)))
                    .thenReturn(customerDto);

            // Act
            Customer result = customerService.updateCustomer(CUSTOMER_ID, null);

            // Assert
            assertThat(result).isNotNull();
            verify(customerMapper, times(1)).updateFromDtoPartially(null, customerEntity);
        }

        @Test
        @DisplayName("Edge case: throws EntityNotFoundException when customer does not exist")
        void shouldThrowWhenCustomerNotFound() {
            // Arrange
            when(customerRepository.findCustomerByCustomerId(CUSTOMER_ID))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(EntityNotFoundException.class,
                    () -> customerService.updateCustomer(CUSTOMER_ID, customerDto));

            verify(customerMapper, never()).updateFromDtoPartially(any(), any());
        }
    }

    // ------------------------------------------------------------------
    // deleteCustomer
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("deleteCustomer")
    class DeleteCustomer {

        @Test
        @DisplayName("Happy path: soft-deletes an active customer")
        void shouldSoftDeleteActiveCustomer() {
            // Arrange
            when(customerRepository.findCustomerByCustomerId(CUSTOMER_ID))
                    .thenReturn(Optional.of(customerEntity));
            when(customerRepository.save(any(CustomerEntity.class)))
                    .thenReturn(customerEntity);

            // Act
            customerService.deleteCustomer(CUSTOMER_ID);

            // Assert
            ArgumentCaptor<CustomerEntity> captor = ArgumentCaptor.forClass(CustomerEntity.class);
            verify(customerRepository, times(1)).save(captor.capture());
            assertThat(captor.getValue().getIsActive()).isFalse();
        }

        @Test
        @DisplayName("Edge case: throws EntityNotFoundException when customer does not exist")
        void shouldThrowWhenCustomerNotFound() {
            // Arrange
            when(customerRepository.findCustomerByCustomerId(CUSTOMER_ID))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(EntityNotFoundException.class,
                    () -> customerService.deleteCustomer(CUSTOMER_ID));

            verify(customerRepository, never()).save(any(CustomerEntity.class));
        }

        @Test
        @DisplayName("Edge case: throws EntityAlreadyDeletedException when customer is already inactive")
        void shouldThrowWhenCustomerAlreadyInactive() {
            // Arrange
            customerEntity.setIsActive(false);
            when(customerRepository.findCustomerByCustomerId(CUSTOMER_ID))
                    .thenReturn(Optional.of(customerEntity));

            // Act & Assert
            EntityAlreadyDeletedException exception = assertThrows(EntityAlreadyDeletedException.class,
                    () -> customerService.deleteCustomer(CUSTOMER_ID));

            assertThat(exception.getMessage()).contains(String.valueOf(CUSTOMER_ID));
            verify(customerRepository, never()).save(any(CustomerEntity.class));
        }
    }
}
