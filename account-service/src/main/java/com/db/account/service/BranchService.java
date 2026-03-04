package com.db.account.service;

import com.db.account.exceptions.EntityAlreadyDeletedException;
import com.db.account.exceptions.EntityNotFoundException;
import com.db.account.mapper.AccountMapper;
import com.db.account.model.AccountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.db.account.entity.BranchEntity;
import com.db.account.model.BranchDto;
import com.db.account.repository.BranchRepository;
import com.db.account.mapper.BranchMapper;
import com.db.account.mapper.CycleAvoidingMappingContext;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class BranchService {

    private final BranchRepository branchRepository;

    private final BranchMapper branchMapper;

    private final AccountMapper accountMapper;

    public BranchDto createBranch(BranchDto branchDto){

        log.atInfo().log("creating a new branch");

        BranchEntity newBranch = branchRepository.save(branchMapper.toEntity(branchDto, new CycleAvoidingMappingContext()));
        return branchMapper.toDto(newBranch, new CycleAvoidingMappingContext());
    }


    public BranchDto getBranchByBranchCode(Long branchCode) {

        log.atInfo().log("getting branch by branch code: {}", branchCode);

        Optional<BranchEntity> branch = branchRepository.findBranchByBranchCode(branchCode);

        if (branch.isEmpty() || !branch.get().getIsActive()) {
            log.atError().log("branch with branch code not found: {}", branchCode);

            throw new EntityNotFoundException("Branch with the given Id not found or Branch is Inactive: " + branchCode);
        }
        return branchMapper.toDto(branch.get(), new CycleAvoidingMappingContext());
    }

    public List<AccountDto> getAllAccountsForABranch(Long branchCode){

        log.atInfo().log("getting all accounts for a branch with branch code: {}", branchCode);

        Optional<BranchEntity> branch = branchRepository.findBranchByBranchCode(branchCode);

        return branch
                .map(branchEntity -> branchEntity.getAccountEntities()
                        .stream()
                        .map(accountEntity -> accountMapper.toDto(accountEntity, new CycleAvoidingMappingContext()))
                        .toList())
                .orElseThrow(() -> new EntityNotFoundException("Branch with the given Id not found:{} " + branchCode));
    }

    public List<BranchDto> getAllBranches(){

        log.atInfo().log("getting all branches");

        List<BranchEntity> activeBranches = branchRepository.findAllByIsActiveTrue();

        return activeBranches.stream()
                .map(branch ->
                        branchMapper.toDto(branch, new CycleAvoidingMappingContext()))
                .toList();
    }

    public BranchDto updateBranch(Long branchCode, BranchDto branchDto){

        log.atInfo().log("updating branch with branch code: {}", branchCode);

        BranchEntity branchEntity = branchRepository.findBranchByBranchCode(branchCode)
                .orElseThrow(() -> new EntityNotFoundException("Branch with the given Id not found: " + branchCode));

        branchMapper.updateFromDtoPartially(branchDto, branchEntity);
        branchRepository.save(branchEntity);
        return branchMapper.toDto(branchEntity, new CycleAvoidingMappingContext());
    }

    public void deleteBranch(Long branchCode){

        log.atInfo().log("deleting branch with branch code: {}", branchCode);

        branchRepository.findBranchByBranchCode(branchCode)
                .map(branchEntity -> {
                    if (!branchEntity.getIsActive()) {
                        throw new EntityAlreadyDeletedException("Branch with the given Id is not found:{} " + branchCode);
                    }
                    branchEntity.setIsActive(false);
                    return branchRepository.save(branchEntity);
                })
                .orElseThrow(() -> new EntityNotFoundException("Branch with the given Id not found:{} " + branchCode));
    }
}
