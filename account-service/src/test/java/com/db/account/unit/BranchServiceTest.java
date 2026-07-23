package com.db.account.unit;

import com.db.account.entity.AccountEntity;
import com.db.account.entity.BranchEntity;
import com.db.account.service.BranchService;
import com.db.account.mapper.AccountMapper;
import com.db.account.mapper.BranchMapper;
import com.db.account.mapper.CycleAvoidingMappingContext;
import com.db.account.repository.BranchRepository;
import com.digital.backend.exceptions.EntityAlreadyDeletedException;
import com.digital.backend.exceptions.EntityNotFoundException;
import com.digital.backend.model.Account;
import com.digital.backend.model.Branch;
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
 * Unit tests for {@link BranchService}.
 * <p>
 * Pure Mockito tests (no Spring context loaded). Downstream collaborators
 * (repository, mappers) are mocked; only the service logic under test is real.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BranchService Unit Tests")
class BranchServiceTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private BranchMapper branchMapper;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private BranchService branchService;

    // Common fixtures reused across tests
    private BranchEntity branchEntity;
    private Branch branchDto;
    private AccountEntity accountEntity;
    private Account accountDto;

    private static final Long BRANCH_CODE = 100L;
    private static final Long ACCOUNT_NUMBER = 300L;

    @BeforeEach
    void setUp() {
        // NOTE: accountEntities is explicitly initialized here because Lombok's
        // @Builder bypasses the field's inline "= new ArrayList<>()" initializer,
        // which would otherwise leave the list null.
        branchEntity = BranchEntity.builder()
                .id(1L)
                .branchCode(BRANCH_CODE)
                .branchName("Main Branch")
                .branchAddress("123 Main St")
                .branchIFSC("IFSC0001")
                .accountEntities(new ArrayList<>())
                .isActive(true)
                .build();

        branchDto = Branch.builder()
                .branchCode(BRANCH_CODE)
                .branchName("Main Branch")
                .branchAddress("123 Main St")
                .branchIFSC("IFSC0001")
                .build();

        accountEntity = AccountEntity.builder()
                .id(1L)
                .accountNumber(ACCOUNT_NUMBER)
                .accountType("SAVINGS")
                .accountBalance(1000.0)
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
    // createBranch
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("createBranch")
    class CreateBranch {

        @Test
        @DisplayName("Happy path: creates and returns the new branch")
        void shouldCreateBranchSuccessfully() {
            // Arrange
            when(branchMapper.toEntity(eq(branchDto), any(CycleAvoidingMappingContext.class)))
                    .thenReturn(branchEntity);
            when(branchRepository.save(any(BranchEntity.class)))
                    .thenReturn(branchEntity);
            when(branchMapper.toDto(eq(branchEntity), any(CycleAvoidingMappingContext.class)))
                    .thenReturn(branchDto);

            // Act
            Branch result = branchService.createBranch(branchDto);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getBranchCode()).isEqualTo(BRANCH_CODE);
            assertThat(result.getBranchName()).isEqualTo("Main Branch");
            verify(branchRepository, times(1)).save(any(BranchEntity.class));
        }

        @Test
        @DisplayName("Edge case: propagates mapper's entity to repository unmodified")
        void shouldPersistMappedEntity() {
            // Arrange
            when(branchMapper.toEntity(eq(branchDto), any(CycleAvoidingMappingContext.class)))
                    .thenReturn(branchEntity);
            when(branchRepository.save(any(BranchEntity.class)))
                    .thenReturn(branchEntity);
            when(branchMapper.toDto(eq(branchEntity), any(CycleAvoidingMappingContext.class)))
                    .thenReturn(branchDto);

            // Act
            branchService.createBranch(branchDto);

            // Assert
            ArgumentCaptor<BranchEntity> captor = ArgumentCaptor.forClass(BranchEntity.class);
            verify(branchRepository).save(captor.capture());
            assertThat(captor.getValue().getBranchCode()).isEqualTo(BRANCH_CODE);
        }
    }

    // ------------------------------------------------------------------
    // getBranchByBranchCode
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("getBranchByBranchCode")
    class GetBranchByBranchCode {

        @Test
        @DisplayName("Happy path: returns branch when found and active")
        void shouldReturnBranchWhenFoundAndActive() {
            // Arrange
            when(branchRepository.findBranchByBranchCode(BRANCH_CODE))
                    .thenReturn(Optional.of(branchEntity));
            when(branchMapper.toDto(eq(branchEntity), any(CycleAvoidingMappingContext.class)))
                    .thenReturn(branchDto);

            // Act
            Branch result = branchService.getBranchByBranchCode(BRANCH_CODE);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getBranchCode()).isEqualTo(BRANCH_CODE);
        }

        @Test
        @DisplayName("Edge case: throws EntityNotFoundException when branch does not exist")
        void shouldThrowWhenBranchNotFound() {
            // Arrange
            when(branchRepository.findBranchByBranchCode(BRANCH_CODE))
                    .thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> branchService.getBranchByBranchCode(BRANCH_CODE));

            assertThat(exception.getMessage()).contains("not found or Branch is Inactive");
            verify(branchMapper, never()).toDto(any(BranchEntity.class), any(CycleAvoidingMappingContext.class));
        }

        @Test
        @DisplayName("Edge case: throws EntityNotFoundException when branch is inactive")
        void shouldThrowWhenBranchIsInactive() {
            // Arrange
            branchEntity.setIsActive(false);
            when(branchRepository.findBranchByBranchCode(BRANCH_CODE))
                    .thenReturn(Optional.of(branchEntity));

            // Act & Assert
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> branchService.getBranchByBranchCode(BRANCH_CODE));

            assertThat(exception.getMessage()).contains(String.valueOf(BRANCH_CODE));
            verify(branchMapper, never()).toDto(any(BranchEntity.class), any(CycleAvoidingMappingContext.class));
        }
    }

    // ------------------------------------------------------------------
    // getAllAccountsForABranch
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("getAllAccountsForABranch")
    class GetAllAccountsForABranch {

        @Test
        @DisplayName("Happy path: returns mapped accounts belonging to the branch")
        void shouldReturnAccountsForBranch() {
            // Arrange
            branchEntity.getAccountEntities().add(accountEntity);

            when(branchRepository.findBranchByBranchCode(BRANCH_CODE))
                    .thenReturn(Optional.of(branchEntity));
            when(accountMapper.toDto(eq(accountEntity), any(CycleAvoidingMappingContext.class)))
                    .thenReturn(accountDto);

            // Act
            List<Account> result = branchService.getAllAccountsForABranch(BRANCH_CODE);

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getAccountNumber()).isEqualTo(ACCOUNT_NUMBER);
        }

        @Test
        @DisplayName("Edge case: returns empty list when branch has no accounts")
        void shouldReturnEmptyListWhenBranchHasNoAccounts() {
            // Arrange - branchEntity fixture already has an empty accountEntities list
            when(branchRepository.findBranchByBranchCode(BRANCH_CODE))
                    .thenReturn(Optional.of(branchEntity));

            // Act
            List<Account> result = branchService.getAllAccountsForABranch(BRANCH_CODE);

            // Assert
            assertThat(result).isEmpty();
            verify(accountMapper, never()).toDto(any(AccountEntity.class), any(CycleAvoidingMappingContext.class));
        }

        @Test
        @DisplayName("Edge case: throws EntityNotFoundException when branch does not exist")
        void shouldThrowWhenBranchNotFound() {
            // Arrange
            when(branchRepository.findBranchByBranchCode(BRANCH_CODE))
                    .thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> branchService.getAllAccountsForABranch(BRANCH_CODE));

            assertThat(exception.getMessage()).contains(String.valueOf(BRANCH_CODE));
        }
    }

    // ------------------------------------------------------------------
    // getAllBranches
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("getAllBranches")
    class GetAllBranches {

        @Test
        @DisplayName("Happy path: returns mapped list of active branches")
        void shouldReturnAllActiveBranches() {
            // Arrange
            BranchEntity secondEntity = BranchEntity.builder()
                    .id(2L)
                    .branchCode(101L)
                    .branchName("Second Branch")
                    .branchAddress("456 Side St")
                    .branchIFSC("IFSC0002")
                    .accountEntities(new ArrayList<>())
                    .isActive(true)
                    .build();
            Branch secondDto = Branch.builder()
                    .branchCode(101L)
                    .branchName("Second Branch")
                    .branchAddress("456 Side St")
                    .branchIFSC("IFSC0002")
                    .build();

            when(branchRepository.findAllByIsActiveTrue())
                    .thenReturn(List.of(branchEntity, secondEntity));
            when(branchMapper.toDto(any(BranchEntity.class), any(CycleAvoidingMappingContext.class)))
                    .thenReturn(branchDto, secondDto);

            // Act
            List<Branch> result = branchService.getAllBranches();

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result).extracting(Branch::getBranchCode)
                    .containsExactly(BRANCH_CODE, 101L);
            verify(branchRepository, times(1)).findAllByIsActiveTrue();
        }

        @Test
        @DisplayName("Edge case: returns empty list when no active branches exist")
        void shouldReturnEmptyListWhenNoActiveBranches() {
            // Arrange
            when(branchRepository.findAllByIsActiveTrue())
                    .thenReturn(Collections.emptyList());

            // Act
            List<Branch> result = branchService.getAllBranches();

            // Assert
            assertThat(result).isEmpty();
            verify(branchMapper, never()).toDto(any(BranchEntity.class), any(CycleAvoidingMappingContext.class));
        }
    }

    // ------------------------------------------------------------------
    // updateBranch
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("updateBranch")
    class UpdateBranch {

        @Test
        @DisplayName("Happy path: updates branch fields via mapper and persists")
        void shouldUpdateBranchSuccessfully() {
            // Arrange
            Branch updateDto = Branch.builder()
                    .branchName("Renamed Branch")
                    .build();

            when(branchRepository.findBranchByBranchCode(BRANCH_CODE))
                    .thenReturn(Optional.of(branchEntity));
            when(branchRepository.save(any(BranchEntity.class)))
                    .thenReturn(branchEntity);
            when(branchMapper.toDto(eq(branchEntity), any(CycleAvoidingMappingContext.class)))
                    .thenReturn(updateDto);

            // Act
            Branch result = branchService.updateBranch(BRANCH_CODE, updateDto);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getBranchName()).isEqualTo("Renamed Branch");
            verify(branchMapper, times(1)).updateFromDtoPartially(updateDto, branchEntity);
            verify(branchRepository, times(1)).save(branchEntity);
        }

        @Test
        @DisplayName("Edge case: throws EntityNotFoundException when branch does not exist")
        void shouldThrowWhenBranchNotFound() {
            // Arrange
            when(branchRepository.findBranchByBranchCode(BRANCH_CODE))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(EntityNotFoundException.class,
                    () -> branchService.updateBranch(BRANCH_CODE, branchDto));

            verify(branchMapper, never()).updateFromDtoPartially(any(), any());
            verify(branchRepository, never()).save(any(BranchEntity.class));
        }
    }

    // ------------------------------------------------------------------
    // deleteBranch
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("deleteBranch")
    class DeleteBranch {

        @Test
        @DisplayName("Happy path: soft-deletes an active branch")
        void shouldSoftDeleteActiveBranch() {
            // Arrange
            when(branchRepository.findBranchByBranchCode(BRANCH_CODE))
                    .thenReturn(Optional.of(branchEntity));
            when(branchRepository.save(any(BranchEntity.class)))
                    .thenReturn(branchEntity);

            // Act
            branchService.deleteBranch(BRANCH_CODE);

            // Assert
            ArgumentCaptor<BranchEntity> captor = ArgumentCaptor.forClass(BranchEntity.class);
            verify(branchRepository, times(1)).save(captor.capture());
            assertThat(captor.getValue().getIsActive()).isFalse();
        }

        @Test
        @DisplayName("Edge case: throws EntityNotFoundException when branch does not exist")
        void shouldThrowWhenBranchNotFound() {
            // Arrange
            when(branchRepository.findBranchByBranchCode(BRANCH_CODE))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(EntityNotFoundException.class,
                    () -> branchService.deleteBranch(BRANCH_CODE));

            verify(branchRepository, never()).save(any(BranchEntity.class));
        }

        @Test
        @DisplayName("Edge case: throws EntityAlreadyDeletedException when branch is already inactive")
        void shouldThrowWhenBranchAlreadyInactive() {
            // Arrange
            branchEntity.setIsActive(false);
            when(branchRepository.findBranchByBranchCode(BRANCH_CODE))
                    .thenReturn(Optional.of(branchEntity));

            // Act & Assert
            EntityAlreadyDeletedException exception = assertThrows(EntityAlreadyDeletedException.class,
                    () -> branchService.deleteBranch(BRANCH_CODE));

            assertThat(exception.getMessage()).contains(String.valueOf(BRANCH_CODE));
            verify(branchRepository, never()).save(any(BranchEntity.class));
        }
    }
}